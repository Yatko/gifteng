package com.venefica.service;

import com.venefica.common.MailException;
import com.venefica.common.RandomGenerator;
import com.venefica.config.AppConfig;
import com.venefica.config.Constants;
import com.venefica.dao.AddressWrapperDao;
import com.venefica.dao.BusinessCategoryDao;
import com.venefica.dao.ImageDao;
import com.venefica.dao.InvitationDao;
import com.venefica.dao.UserPointDao;
import com.venefica.dao.UserVerificationDao;
import com.venefica.model.BusinessCategory;
import com.venefica.model.BusinessUserData;
import com.venefica.model.Invitation;
import com.venefica.model.MemberUserData;
import com.venefica.model.NotificationType;
import com.venefica.model.User;
import com.venefica.model.UserPoint;
import com.venefica.model.UserSetting;
import com.venefica.model.UserVerification;
import com.venefica.service.dto.BusinessCategoryDto;
import com.venefica.service.dto.UserDto;
import com.venefica.service.dto.UserSettingDto;
import com.venefica.service.dto.UserStatisticsDto;
import com.venefica.service.fault.GeneralException;
import com.venefica.service.fault.InvalidInvitationException;
import com.venefica.service.fault.InvitationNotFoundException;
import com.venefica.service.fault.UserAlreadyExistsException;
import com.venefica.service.fault.UserField;
import com.venefica.service.fault.UserNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.jws.WebService;
import org.springframework.social.connect.Connection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

@Service("userManagementService")
@WebService(endpointInterface = "com.venefica.service.UserManagementService")
public class UserManagementServiceImpl extends AbstractService implements UserManagementService {
    
    @Inject
    private AdService adService;
    @Inject
    private MessageService messageService;
    
    @Inject
    private AppConfig appConfig;
    @Inject
    private ImageDao imageDao;
    @Inject
    private InvitationDao invitationDao;
    @Inject
    private BusinessCategoryDao businessCategoryDao;
    @Inject
    private AddressWrapperDao addressWrapperDao;
    @Inject
    private UserPointDao userPointDao;
    @Inject
    private UserVerificationDao userVerificationDao;
    
    //*****************************
    //* user verification related *
    //*****************************
    
    @Override
    @Transactional
    public void verifyUser(String code) throws UserNotFoundException, GeneralException {
        UserVerification userVerification = userVerificationDao.findByCode(code);
        if ( userVerification == null ) {
            throw new GeneralException("Verification code was not found.");
        }
        
        if ( userVerification.isVerified() ) {
            logger.info("Code (" + code + ") is already verified");
            return;
        }
        
        userVerification.setVerified(true);
        userVerification.setVerifiedAt(new Date());
        
        User user = userVerification.getUser();
        user.setVerified(true);
        userDao.update(user);
    }
    
    @Override
    @Transactional
    public void resendVerification() throws UserNotFoundException, GeneralException {
        UserVerification userVerification = userVerificationDao.findByUser(getCurrentUserId());
        if ( userVerification == null ) {
            throw new GeneralException("User verification was not found.");
        }
        
        if ( userVerification.isVerified() ) {
            logger.warn("User verification is already done (code: " + userVerification.getCode() + ")");
            return;
        }
        
        sendNotification(userVerification);
    }
    
    
    
    //**********************
    //* categories related *
    //**********************
    
    @Override
    public List<BusinessCategoryDto> getAllBusinessCategories() {
        List<BusinessCategoryDto> result = new LinkedList<BusinessCategoryDto>();
        List<BusinessCategory> categories = businessCategoryDao.getCategories();
        
        for (BusinessCategory category : categories) {
            BusinessCategoryDto categoryDto = new BusinessCategoryDto(category);
            result.add(categoryDto);
        }
        
        return result;
    }
    
    
    
    //************************************
    //* user crud (create/update/delete) *
    //************************************
    
    @Override
    @Transactional
    public Long registerBusinessUser(UserDto userDto, String password) throws UserAlreadyExistsException, GeneralException {
        String email = userDto.getEmail();
        String businessName = userDto.getBusinessName();
        Long categoryId = userDto.getBusinessCategoryId();
        
        if (userDataDao.findByBusinessName(businessName) != null) {
            throw new UserAlreadyExistsException(UserField.NAME, "User with the specified business name already exists!");
        } else if (userDao.findUserByEmail(email) != null) {
            throw new UserAlreadyExistsException(UserField.EMAIL, "User with the specified email already exists!");
        }
        
        BusinessCategory category = validateBusinessCategory(categoryId);
        
        User user = userDto.toBusinessUser(imageDao, addressWrapperDao);
        user.setPassword(password);
        ((BusinessUserData) user.getUserData()).setCategory(category);
        
        userDataDao.save(user.getUserData());
        return userDao.save(user);
    }
    
    @Override
    @Transactional
    public Long registerUser(UserDto userDto, String password, String invitationCode) throws UserAlreadyExistsException, InvitationNotFoundException, InvalidInvitationException, GeneralException {
        // Check for existing users
        if (userDao.findUserByName(userDto.getName()) != null) {
            throw new UserAlreadyExistsException(UserField.NAME, "User with the same name already exists!");
        } else if (userDao.findUserByEmail(userDto.getEmail()) != null) {
            throw new UserAlreadyExistsException(UserField.EMAIL, "User with the specified email already exists!");
        } else if ( invitationCode == null ) {
            throw new InvalidInvitationException("Invitation code cannot be empty!");
        }
        
        Invitation invitation = invitationDao.findByCode(invitationCode);
        if (invitation == null) {
            throw new InvitationNotFoundException("Invitation with code '" + invitationCode + "' not found!");
        } else if ( !invitation.isValid() ) {
            throw new InvalidInvitationException("Invitation with code '" + invitationCode + "' is invalid!");
        }
        
        User user = userDto.toMemberUser(imageDao, addressWrapperDao);
        user.setPassword(password);
        user.setVerified(userDto.getEmail().trim().equals(invitation.getEmail().trim()));
        
        invitation.use();
        invitationDao.update(invitation);
        
        UserSetting userSetting = createUserSetting(null);
        MemberUserData userData = ((MemberUserData) user.getUserData());
        userData.setInvitation(invitation);
        userData.setUserSetting(userSetting);
        userDataDao.save(userData);
        
        UserPoint userPoint = new UserPoint(appConfig.getRequestStartupLimit(), 0, 0);
        userPoint.setUser(user);
        userPointDao.save(userPoint);
        
        user.setUserPoint(userPoint);
        Long userId = userDao.save(user);
        
        if ( !user.isVerified() ) {
            UserVerification userVerification = new UserVerification();
            userVerification.setCode(getUserVerificationCode());
            userVerification.setUser(user);
            userVerificationDao.save(userVerification);
            
            sendNotification(userVerification);
        }
        
        return userId;
    }
    
    @Override
    public boolean isUserComplete() {
        User user = getCurrentUser();
        return user.isComplete();
    }

    @Override
    @Transactional
    public boolean updateUser(UserDto userDto) throws UserAlreadyExistsException {
        User user = getCurrentUser();
        User userWithTheSameName = userDao.findUserByName(userDto.getName());

        if (userWithTheSameName != null && !userWithTheSameName.getId().equals(user.getId())) {
            throw new UserAlreadyExistsException(UserField.NAME, "User with the same name already exists!");
        }

        User userWithTheSameEmail = userDao.findUserByEmail(userDto.getEmail());

        if (userWithTheSameEmail != null && !userWithTheSameEmail.getId().equals(user.getId())) {
            throw new UserAlreadyExistsException(UserField.EMAIL, "User with the same email already exists!");
        }

        userDto.update(user, imageDao, addressWrapperDao);
        userDataDao.update(user.getUserData());

        return user.isComplete();
    }
    
    
    
    //*******************
    //* user statistics *
    //*******************
    
    @Override
    @Transactional
    public UserStatisticsDto getStatistics(Long userId) throws UserNotFoundException {
        User user = validateUser(userId);
        return buildStatistics(user);
    }
    
    
    
    //***************
    //* user search *
    //***************
    
    @Override
    @Transactional
    public List<UserDto> getTopUsers(int numberUsers) {
        User currentUser = getCurrentUser();
        List<UserDto> result = new LinkedList<UserDto>();
        List<User> users = userDao.getTopUsers(numberUsers);
        
        for (User user : users) {
            UserDto userDto = new UserDto(user);
            populateRelations(userDto, currentUser, user);
            result.add(userDto);
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public UserDto getUser() throws UserNotFoundException {
        User user = getCurrentUser();
        
        if ( user == null ) {
            Long userId = getCurrentUserId();
            logger.error("Getting user (userId: " + userId + ") failed");
            throw new UserNotFoundException("User with ID '" + userId + "' not found!");
        }
        
        return new UserDto(user);
    }

    @Override
    @Transactional
    public UserDto getUserByName(String name) throws UserNotFoundException {
        User user = validateUser(name);
        User currentUser = getCurrentUser();
        UserStatisticsDto statistics = buildStatistics(user);
        
        UserDto userDto = new UserDto(user);
        userDto.setStatistics(statistics);
        populateRelations(userDto, currentUser, user);
        return userDto;
    }
    
    @Override
    @Transactional
    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        User user = userDao.findUserByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User with email '" + email + "' not found!");
        }

        User currentUser = getCurrentUser();
        UserStatisticsDto statistics = buildStatistics(user);
        
        UserDto userDto = new UserDto(user);
        userDto.setStatistics(statistics);
        populateRelations(userDto, currentUser, user);
        return userDto;
    }
    
    @Override
    @Transactional
    public UserDto getUserByPhone(String phone) throws UserNotFoundException {
        User user = userDao.findUserByPhoneNumber(phone);

        if (user == null) {
            throw new UserNotFoundException("User with phone number '" + phone + "' not found!");
        }

        User currentUser = getCurrentUser();
        UserStatisticsDto statistics = buildStatistics(user);
        
        UserDto userDto = new UserDto(user);
        userDto.setStatistics(statistics);
        populateRelations(userDto, currentUser, user);
        return userDto;
    }
    
    @Override
    @Transactional
    public UserDto getUserById(Long userId) throws UserNotFoundException {
        User user = validateUser(userId);
        User currentUser = getCurrentUser();
        UserStatisticsDto statistics = buildStatistics(user);
        
        UserDto userDto = new UserDto(user);
        userDto.setStatistics(statistics);
        populateRelations(userDto, currentUser, user);
        return userDto;
    }

    
    
    //***************
    //* user follow *
    //***************

    @Override
    @Transactional
    public UserStatisticsDto follow(Long userId) throws UserNotFoundException {
        User user = getCurrentUser();
        User following = userDao.get(userId);
        
        if ( following == null ) {
            throw new UserNotFoundException("Cannot find the given following (userId: " + userId + ") user.");
        }
        
        user.addFollowing(following);
        
        Map<String, Object> vars = new HashMap<String, Object>(0);
        vars.put("user", following);
        vars.put("follower", user);

        emailSender.sendNotification(NotificationType.FOLLOWER_ADDED, following, vars);
        
        return buildStatistics(user);
    }
    
    @Override
    @Transactional
    public UserStatisticsDto unfollow(Long userId) throws UserNotFoundException {
        User user = getCurrentUser();
        User following = userDao.get(userId);
        
        if ( following == null ) {
            throw new UserNotFoundException("Cannot find the given following (userId: " + userId + ") user.");
        }
        
        user.removeFollowing(following);
        
        return buildStatistics(user);
    }
    
    @Override
    @Transactional
    public List<UserDto> getFollowers(Long userId) throws UserNotFoundException {
        List<UserDto> result = new LinkedList<UserDto>();
        User user = validateUser(userId);
        User currentUser = getCurrentUser();
        
        if ( user.getFollowers() != null ) {
            for ( User follower : user.getFollowers() ) {
                UserDto userDto = new UserDto(follower);
                populateRelations(userDto, currentUser, follower);
                
                result.add(userDto);
            }
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public List<UserDto> getFollowings(Long userId) throws UserNotFoundException {
        List<UserDto> result = new LinkedList<UserDto>();
        User user = validateUser(userId);
        User currentUser = getCurrentUser();
        
        if ( user.getFollowings() != null ) {
            for ( User following : user.getFollowings()) {
                UserDto userDto = new UserDto(following);
                populateRelations(userDto, currentUser, following);
                
                result.add(userDto);
            }
        }
        
        return result;
    }
    
    
    
    //*****************
    //* user settings *
    //*****************
    
    @Override
    @Transactional
    public UserSettingDto getUserSetting() throws GeneralException {
        User currentUser = getCurrentUser();
        
        if ( currentUser.isBusinessAccount() ) {
            throw new GeneralException("User is a business type, there is no setting for it.");
        }
        
        MemberUserData userData = (MemberUserData) currentUser.getUserData();
        UserSetting userSetting = userData.getUserSetting();
        
        if ( userSetting == null ) {
            //automatically creating user setting if not present
            userSetting = createUserSetting(userData);
        }
        
        return new UserSettingDto(currentUser, userSetting);
    }
    
    @Override
    @Transactional
    public void saveUserSetting(UserSettingDto userSettingDto) throws GeneralException {
        User currentUser = getCurrentUser();
        
        if ( currentUser.isBusinessAccount() ) {
            throw new GeneralException("User is a business type, there is no setting for it.");
        }
        
        MemberUserData userData = (MemberUserData) currentUser.getUserData();
        UserSetting userSetting = userData.getUserSetting();
        
        if ( userSetting == null ) {
            //automatically creating user setting if not present
            userSetting = createUserSetting(userData);
        }
        
        userSetting.setNotifiableTypes(userSettingDto.getNotifiableTypes());
        userSettingDao.update(userSetting);
    }
    
    
    
    //******************
    //* social network *
    //******************
    
    @Override
    public Set<String> getConnectedSocialNetworks() {
        MultiValueMap<String, Connection<?>> allConnections = connectionRepository.findAllConnections();
        Set<String> result = new HashSet<String>();

        for (String network : allConnections.keySet()) {
            List<Connection<?>> connections = allConnections.get(network);
            if (!connections.isEmpty()) {
                result.add(network);
            }
        }
        return result;
    }

    @Override
    public void disconnectFromNetwork(String networkName) {
        connectionRepository.removeConnections(networkName);
    }
    
    // internal helpers
    
    private BusinessCategory validateBusinessCategory(Long categoryId) throws GeneralException {
        if (categoryId == null) {
            throw new GeneralException("Category id not specified!");
        }

        BusinessCategory category = businessCategoryDao.get(categoryId);
        if (category == null) {
            throw new GeneralException("Category with id = " + categoryId + " not found!");
        }
        return category;
    }
    
    private void populateRelations(UserDto userDto, User currentUser, User user) {
        //populates only when the user and currentUser is not the same person
        if ( !currentUser.equals(user) ) {
            userDto.setInFollowers(currentUser.inFollowers(user));
            userDto.setInFollowings(currentUser.inFollowings(user));
        }
    }

    private int getFollowersSize(User user) throws UserNotFoundException {
        return user.getFollowers() != null ? user.getFollowers().size() : 0;
    }
    
    private int getFollowingsSize(User user) throws UserNotFoundException {
        return user.getFollowings() != null ? user.getFollowings().size() : 0;
    }
    
    private UserStatisticsDto buildStatistics(User user) throws UserNotFoundException {
        Long userId = user.getId();
        int numReceivings = adService.getUserRequestedAdsSize(userId);
        int numGivings = adService.getUserAdsSize(userId);
        int numRatings = adService.getReceivedRatingsSize(userId);
        int numBookmarks = adService.getBookmarkedAdsSize(userId);
        int numFollowers = this.getFollowersSize(user);
        int numFollowings = this.getFollowingsSize(user);
        int numUnreadMessages = messageService.getUnreadMessagesSize(userId);
        int requestLimit = user.getUserPoint() != null ? user.getUserPoint().getRequestLimit() : 0;
        
        UserStatisticsDto statistics = new UserStatisticsDto();
        statistics.setNumReceivings(numReceivings);
        statistics.setNumGivings(numGivings);
        statistics.setNumBookmarks(numBookmarks);
        statistics.setNumFollowers(numFollowers);
        statistics.setNumFollowings(numFollowings);
        statistics.setNumRatings(numRatings);
        statistics.setNumUnreadMessages(numUnreadMessages);
        statistics.setRequestLimit(requestLimit);
        return statistics;
    }
    
    private String getUserVerificationCode() throws GeneralException {
        String code;
        int generationTried = 0;
        while ( true ) {
            code = RandomGenerator.generateAlphanumeric(Constants.USER_VERIFICATION_DEFAULT_CODE_LENGTH);
            generationTried++;
            if ( userVerificationDao.findByCode(code) == null ) {
                //the generated code does not exists, found an unused (free) one
                break;
            } else if ( generationTried >= 10 ) {
                throw new GeneralException("Cannot generate valid user verification code!");
            }
        }
        return code;
    }

    private void sendNotification(UserVerification userVerification) {
        String code = userVerification.getCode();
        String email = userVerification.getUser().getEmail();
        
        try {
            Map<String, Object> vars = new HashMap<String, Object>(0);
            vars.put("code", userVerification.getCode());
            vars.put("user", userVerification.getUser());

            emailSender.sendHtmlEmailByTemplates(
                    Constants.USER_VERIFICATION_REMINDER_SUBJECT_TEMPLATE,
                    Constants.USER_VERIFICATION_REMINDER_HTML_MESSAGE_TEMPLATE,
                    Constants.USER_VERIFICATION_REMINDER_PLAIN_MESSAGE_TEMPLATE,
                    email,
                    vars);
        } catch ( MailException ex ) {
            logger.error("Email exception when sending user verification reminder (email: " + email + ", code: " + code + ")", ex);
        }
    }
}