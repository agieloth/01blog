package io.aotchoun.blog.dto.response;

public class LikeResponse {
    private Long postId;
    private long likeCount;
    private boolean likedByCurrentUser;

    public LikeResponse() {}

    public LikeResponse(Long postId, long likeCount, boolean likedByCurrentUser) {
        this.postId = postId;
        this.likeCount = likeCount;
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public Long getPostId() { return postId; }
    public long getLikeCount() { return likeCount; }
    public boolean isLikedByCurrentUser() { return likedByCurrentUser; }
    public void setPostId(Long postId) { this.postId = postId; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }
    public void setLikedByCurrentUser(boolean likedByCurrentUser) { this.likedByCurrentUser = likedByCurrentUser; }
}