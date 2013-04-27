package com.venefica.service;

import com.venefica.service.dto.UserDto;
import com.venefica.service.fault.InvalidInvitationException;
import com.venefica.service.fault.InvitationNotFoundException;
import com.venefica.service.fault.UserAlreadyExistsException;
import com.venefica.service.fault.UserNotFoundException;
import java.util.List;
import java.util.Set;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

/**
 * Registers new users and updates information about them.
 *
 * @author Sviatoslav Grebenchukov
 */
@WebService(name = "UserManagement", targetNamespace = Namespace.SERVICE)
@SOAPBinding(parameterStyle = ParameterStyle.WRAPPED)
public interface UserManagementService {

    /**
     * Registers new local user not connected to any social network.
     *
     * @param userDto the user to register
     * @param password the password (may be a hash of the password in the
     * future)
     * @param invitationCode the invitation code
     * @throws UserAlreadyExistsException is thrown when a user with the same
     * name already exists
     * @throws InvitationNotFoundException thrown when the provided invitation code
     * could not be found
     */
    @WebMethod(operationName = "RegisterUser")
    @WebResult(name = "userId")
    public Long registerUser(@WebParam(name = "user") UserDto userDto,
            @WebParam(name = "password") String password,
            @WebParam(name = "invitationCode") String invitationCode) throws UserAlreadyExistsException, InvitationNotFoundException, InvalidInvitationException;
    
    /**
     * Updates user information.
     *
     * @param userDto updated user object
     * @return true if all required information is gathered.
     */
    @WebMethod(operationName = "UpdateUser")
    @WebResult(name = "complete")
    public boolean updateUser(@WebParam(name = "user") UserDto userDto)
            throws UserAlreadyExistsException;

    /**
     * Retrieves information about the current (logged) user.
     *
     * @return user object
     * @throws UserNotFoundException is thrown when a user with the specified
     * token (id) not found
     */
    @WebMethod(operationName = "GetUser")
    @WebResult(name = "user")
    public UserDto getUser() throws UserNotFoundException;

    /**
     * Retrieves information about the user by his name.
     *
     * @param name name of the user
     * @return user object
     * @throws UserNotFoundException is thrown when a user with the specified
     * name not found
     */
    @WebMethod(operationName = "GetUserByName")
    @WebResult(name = "user")
    public UserDto getUserByName(@WebParam(name = "name") String name) throws UserNotFoundException;
    
    /**
     * Retrieves information about the user by his email address.
     * 
     * @param email the email address of the user
     * @return user object
     * @throws UserNotFoundException when the user with the specified email
     * address not found
     */
    @WebMethod(operationName = "GetUserByEmail")
    @WebResult(name = "user")
    public UserDto getUserByEmail(@WebParam(name = "email") String email) throws UserNotFoundException;
    
    /**
     * Retrieves information about the user by his phone number.
     * 
     * @param phone the phone number of the user
     * @return user object
     * @throws UserNotFoundException when the user with the specified phone
     * number not found
     */
    @WebMethod(operationName = "GetUserByPhone")
    @WebResult(name = "user")
    public UserDto getUserByPhone(@WebParam(name = "phone") String phone) throws UserNotFoundException;

    /**
     * Returns true if all required information is gathered for the current
     * user.
     */
    @WebMethod(operationName = "IsUserComplete")
    @WebResult(name = "complete")
    public boolean isUserComplete();

    /**
     * Adds the given user into the actual ones followers list.
     * 
     * @param userId the user identifier
     * @throws UserNotFoundException the given user could not be found
     */
    @WebMethod(operationName = "Follow")
    public void follow(@WebParam(name = "userId") Long userId) throws UserNotFoundException;
    
    /**
     * Removes the given user from the followers list of the actual one.
     * 
     * @param userId the user identifier
     * @throws UserNotFoundException the given user could not be found
     */
    @WebMethod(operationName = "Unfollow")
    public void unfollow(@WebParam(name = "userId") Long userId) throws UserNotFoundException;
    
    @WebMethod(operationName = "GetFollowers")
    @WebResult(name = "follower")
    public List<UserDto> getFollowers(@WebParam(name = "userId") Long userId) throws UserNotFoundException;
    
    @WebMethod(operationName = "GetFollowings")
    @WebResult(name = "following")
    public List<UserDto> getFollowings(@WebParam(name = "userId") Long userId) throws UserNotFoundException;
    
    /**
     * Returns a list of social network names connected to the current user
     * account.
     */
    @WebMethod(operationName = "GetConnectedSocialNetworks")
    @WebResult(name = "network")
    public Set<String> getConnectedSocialNetworks();

    /**
     * Removes a connection between the current user account and an account of
     * the specified social network.
     *
     * @param networkName the name of the social network
     */
    @WebMethod(operationName = "DisconnectFromNetwork")
    public void disconnectFromNetwork(@WebParam(name = "networkName") String networkName);
}
