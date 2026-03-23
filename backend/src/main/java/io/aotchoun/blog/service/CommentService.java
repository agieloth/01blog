package io.aotchoun.blog.service;

import io.aotchoun.blog.dto.request.CommentRequest;
import io.aotchoun.blog.dto.response.CommentResponse;
import io.aotchoun.blog.entity.Comment;
import io.aotchoun.blog.entity.Notification;
import io.aotchoun.blog.entity.Post;
import io.aotchoun.blog.entity.User;
import io.aotchoun.blog.exception.ResourceNotFoundException;
import io.aotchoun.blog.exception.UnauthorizedException;
import io.aotchoun.blog.repository.CommentRepository;
import io.aotchoun.blog.repository.PostRepository;
import io.aotchoun.blog.repository.UserRepository;
import io.aotchoun.blog.service.PostService.WsEvent;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository,
                          PostRepository postRepository,
                          UserRepository userRepository,
                          SimpMessagingTemplate messagingTemplate,
                          NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId) {
        // Vérifier que le post existe
        if (!postRepository.existsById(postId)) {
            throw new ResourceNotFoundException("Post not found with id: " + postId);
        }
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    public CommentResponse addComment(Long postId, CommentRequest request, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        Comment comment = new Comment(request.getContent(), author, post);
        comment = commentRepository.save(comment);

        // ✅ Créer une notification si ce n'est pas l'auteur qui commente son propre post
        if (!post.getAuthor().getId().equals(author.getId())) {
            notificationService.createNotification(
                post.getAuthor().getId(),
                Notification.NotificationType.POST_COMMENTED,
                author.getUsername() + " a commenté votre post : " + post.getTitle(),
                postId,
                author.getUsername()
            );
        }

        CommentResponse response = CommentResponse.from(comment);

        // Broadcaster sur le topic du post concerné
        messagingTemplate.convertAndSend("/topic/comments/" + postId,
            new WsEvent("COMMENT_ADDED", response));
        // Broadcaster le nouveau count à tous
        long commentCount = commentRepository.countByPostId(postId);
        messagingTemplate.convertAndSend("/topic/posts",
            new WsEvent("COMMENT_COUNT_UPDATED", new CommentCountUpdate(postId, commentCount)));
        return response;
    }

    public void deleteComment(Long postId, Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        if (!comment.getPost().getId().equals(postId)) {
            throw new ResourceNotFoundException("Comment not found in this post");
        }
        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("You can only delete your own comments");
        }
        commentRepository.deleteById(commentId);

        messagingTemplate.convertAndSend("/topic/comments/" + postId,
            new WsEvent("COMMENT_DELETED", commentId));
        long commentCount = commentRepository.countByPostId(postId);
        messagingTemplate.convertAndSend("/topic/posts",
            new WsEvent("COMMENT_COUNT_UPDATED", new CommentCountUpdate(postId, commentCount)));
    }

    public static class WsEvent {
        private String type; private Object data;
        public WsEvent(String type, Object data) { 
            this.type = type; 
            this.data = data; 
        }

        public String getType() { return type; }
        public Object getData() { return data; }
    }

    public static class CommentCountUpdate {
        private Long postId; 
        private long count;
        public CommentCountUpdate(Long postId, long count) { 
            this.postId = postId; 
            this.count = count; 
        }
        
        public Long getPostId() { return postId; }
        public long getCount() { return count; }
    }
}