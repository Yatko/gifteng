package com.venefica.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import javax.inject.Inject;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.venefica.dao.AdDao;
import com.venefica.dao.CommentDao;
import com.venefica.dao.MessageDao;
import com.venefica.model.Ad;
import com.venefica.model.Comment;
import com.venefica.model.Message;
import com.venefica.service.MessageService;
import com.venefica.service.dto.CommentDto;
import com.venefica.service.dto.MessageDto;
import com.venefica.service.fault.AdNotFoundException;
import com.venefica.service.fault.AuthorizationException;
import com.venefica.service.fault.CommentNotFoundException;
import com.venefica.service.fault.CommentValidationException;
import com.venefica.service.fault.MessageNotFoundException;
import com.venefica.service.fault.MessageValidationException;
import com.venefica.service.fault.UserNotFoundException;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("test")
@ContextConfiguration(locations = "/MessageServiceTest-context.xml")
public class MessageServiceTest extends ServiceTestBase<MessageService> {

	private static final Long TestAdId = new Long(1);
	
//	private static final Long TestMessageId = new Long(1);

	@Inject
	private AdDao adDao;
	@Inject
	private CommentDao commentDao;
	@Inject
	private MessageDao messageDao;

	private Ad ad;
//	private Message message;

	public MessageServiceTest() {
		super(MessageService.class);
	}

	@Before
	public void init() {
		ad = adDao.get(TestAdId);
		assertNotNull(ad);
		
//		message = messageDao.get(TestMessageId);
//		assertNotNull(message);
	}

	@Test
	public void addCommentToAdTest() throws AdNotFoundException, CommentValidationException {
		CommentDto comment = new CommentDto("Test message");

		authenticateClientAsSecondUser();
		Long commentId = client.addCommentToAd(ad.getId(), comment);
		assertNotNull("Comment id must be returned!", commentId);
	}

	@Test(expected = AdNotFoundException.class)
	public void addCommentToUnexistingAdTest() throws AdNotFoundException,
			CommentValidationException {
		CommentDto comment = new CommentDto("Test comment");
		authenticateClientAsFirstUser();
		client.addCommentToAd(new Long(-1), comment);
	}

	@Test(expected = CommentValidationException.class)
	public void addInvalidCommentTest() throws AdNotFoundException, CommentValidationException {
		CommentDto comment = new CommentDto();
		comment.setText(null);

		authenticateClientAsFirstUser();
		client.addCommentToAd(ad.getId(), comment);
	}

	@Test
	public void updateCommentTest() throws AdNotFoundException, CommentValidationException,
			CommentNotFoundException {
		CommentDto comment = new CommentDto("New comment");

		authenticateClientAsFirstUser();
		Long commentId = client.addCommentToAd(ad.getId(), comment);

		comment.setId(commentId);
		comment.setText("Updated comment");
		client.updateComment(comment);

		Comment storedComment = commentDao.get(commentId);

		assertNotNull("Comment not found in the database!", storedComment);
		assertEquals("Comment not updated!", comment.getText(), storedComment.getText());
		assertNotNull("Comment updateAt field not set!", storedComment.getUpdatedAt());
	}

	@Test(expected = CommentNotFoundException.class)
	public void updateUnexistingCommentTest() throws CommentNotFoundException,
			CommentValidationException {
		CommentDto comment = new CommentDto("Some comment");
		comment.setId(new Long(-1));

		authenticateClientAsFirstUser();
		client.updateComment(comment);
	}

	@Test(expected = CommentValidationException.class)
	public void updateInvalidCommentTest() throws AdNotFoundException, CommentValidationException,
			CommentNotFoundException {
		CommentDto comment = new CommentDto("Some comment");
		authenticateClientAsFirstUser();
		Long messageId = client.addCommentToAd(ad.getId(), comment);

		// update with invalid comment
		comment.setId(messageId);
		comment.setText(null);
		client.updateComment(comment);
	}

	@Test
	public void getCommentsByAdTest() throws AdNotFoundException {
		authenticateClientAsFirstUser();
		List<CommentDto> comments = client.getCommentsByAd(ad.getId(), new Long(-1), 10);
		assertNotNull("List of comments must be returned!", comments);
		assertTrue("At least one comment must exist", !comments.isEmpty());
	}

	@Test(expected = UserNotFoundException.class)
	public void sendMessageToUnexistingUserTest() throws UserNotFoundException,
			MessageValidationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = new MessageDto("UnexisingUserName", "Test message");
		client.sendMessage(messageDto);
	}

	@Test(expected = MessageValidationException.class)
	public void sendInvalidMessageTest() throws UserNotFoundException, MessageValidationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = new MessageDto(getSecondUser().getName(), null);
		client.sendMessage(messageDto);
	}

	@Test(expected = MessageValidationException.class)
	public void sendMessageToMyselfTest() throws UserNotFoundException, MessageValidationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = new MessageDto(getFirstUser().getName(), "Test message");
		client.sendMessage(messageDto);
	}

	@Test
	public void sendMessageTest() throws UserNotFoundException, MessageValidationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = new MessageDto(getSecondUser().getName(), "Test message");
		Long messageId = client.sendMessage(messageDto);

		Message message = messageDao.get(messageId);
		assertNotNull("Message with id  = " + messageId + " not found!", message);
		assertNotNull("Id field not set!", message.getId());
		assertTrue("Message text not match", messageDto.getText().equals(message.getText()));
		assertNotNull("CreatedAt field is null!", message.getCreatedAt());
		assertTrue("Message must be marked as not read!", !message.hasRead());
	}
	
	@Test(expected = MessageNotFoundException.class)
	public void updateUnexistingMessageTest() throws MessageNotFoundException, AuthorizationException, MessageValidationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = new MessageDto();
		messageDto.setId(new Long(-1));
		
		client.updateMessage(messageDto);		
	}
	
	@Test(expected = AuthorizationException.class)
	public void updateMessageWithDifferentUserTest() throws MessageNotFoundException, AuthorizationException, MessageValidationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = client.getAllMessages().get(0);		
		authenticateClientAsSecondUser();
		messageDto.setText("updated text");
		client.updateMessage(messageDto);				
	}
	
	@Test(expected = MessageValidationException.class)
	public void updateWithInvalidMessageTest() throws MessageNotFoundException, AuthorizationException, MessageValidationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = client.getAllMessages().get(0);
		messageDto.setText(null);
		client.updateMessage(messageDto);
	}
	
	@Test
	public void updateMessageTest() throws MessageNotFoundException, AuthorizationException, MessageValidationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = client.getAllMessages().get(0);
		messageDto.setText("New Test Message");
		client.updateMessage(messageDto);
	}	

	@Test
	public void getAllMessagesCalledByThirdUserTest() {
		authenticateClientAsThirdUser();
		List<MessageDto> messages = client.getAllMessages();

		assertTrue("Third user can't see messages not addressed to him or not sent by him!",
				messages == null);
	}

	@Test
	public void getAllMessagesTest() {
		authenticateClientAsSecondUser();
		List<MessageDto> messages = client.getAllMessages();

		// only one incoming message should be in the collection
		assertTrue("Invalid number of messages in the collection!", messages.size() == 1);

		MessageDto message = messages.get(0);
		assertTrue("Message must be marked as not read!", !message.hasRead());

		Message storedMessage = messageDao.get(message.getId());
		assertTrue("Message in the database must be marked as read!", storedMessage.hasRead());
	}

	@Test(expected = MessageNotFoundException.class)
	public void hideUnexistingMessageTest() throws MessageNotFoundException, AuthorizationException {
		authenticateClientAsFirstUser();
		client.hideMessage(new Long(-1));
	}

	@Test(expected = AuthorizationException.class)
	public void hideMessageWithTirdUserTest() throws UserNotFoundException,
			MessageValidationException, MessageNotFoundException, AuthorizationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = new MessageDto(getSecondUser().getName(), "Second message");
		Long messageId = client.sendMessage(messageDto);

		authenticateClientAsThirdUser();
		client.hideMessage(messageId);
	}

	@Test
	public void hideMessageTest() throws UserNotFoundException, MessageValidationException,
			MessageNotFoundException, AuthorizationException {
		authenticateClientAsFirstUser();
		MessageDto messageDto = new MessageDto(getSecondUser().getName(), "Third message");
		Long messageId = client.sendMessage(messageDto);
		client.hideMessage(messageId);

		Message updatedMessage = messageDao.get(messageId);
		assertTrue("HiddenBySender must be true!", updatedMessage.isHiddenBySender());
		assertTrue("HiddenByRecipient must be false!", !updatedMessage.isHiddenByRecipient());

		authenticateClientAsSecondUser();
		client.hideMessage(messageId);

		updatedMessage = messageDao.get(messageId);
		assertTrue("HiddenByRecipient must be true!", updatedMessage.isHiddenByRecipient());
	}

	@Test(expected = MessageNotFoundException.class)
	public void deleteUnexistingMessageTest() throws MessageNotFoundException,
			AuthorizationException {
		authenticateClientAsFirstUser();
		client.deleteMessage(new Long(-1));
	}

	@Test(expected = AuthorizationException.class)
	public void deleteMessageByTirdUserTest() throws UserNotFoundException,
			MessageValidationException, MessageNotFoundException, AuthorizationException {
		authenticateClientAsFirstUser();
		Long messageId = client.sendMessage(new MessageDto(getSecondUser().getName(),
				"Four message"));
		authenticateClientAsThirdUser();
		client.deleteMessage(messageId);
	}

	@Test
	public void deleteMessateTest() throws UserNotFoundException, MessageValidationException,
			MessageNotFoundException, AuthorizationException {
		authenticateClientAsFirstUser();
		Long messageId = client.sendMessage(new MessageDto(getSecondUser().getName(),
				"Five message"));
		client.deleteMessage(messageId);

		Message deletedMessage = messageDao.get(messageId);
		assertTrue("Message has not been deleted!", deletedMessage == null);
	}
}