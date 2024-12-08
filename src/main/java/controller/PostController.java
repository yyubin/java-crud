package controller;

import config.annotations.Controller;
import config.annotations.Requires;
import controller.annotations.DeleteMapping;
import controller.annotations.GetMapping;
import controller.annotations.PostMapping;
import controller.annotations.PutMapping;
import domain.Post;
import dto.PostRegisterDTO;
import dto.PostUpdateDTO;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.response.ResponseCode;
import message.PrintResultMessage;
import service.PostService;
import service.interfaces.PostServiceInterface;
import view.PrintHandler;
import view.interfaces.PrintProcessor;

import java.util.HashMap;
import java.util.Map;

@Controller
@Requires(dependsOn = {PostServiceInterface.class})
public class PostController implements ControllerInterface{

    private final PostServiceInterface postService;

    public PostController(PostServiceInterface postService) {
        this.postService = postService;
    }

    @PostMapping(path = "/posts/add")
    public HttpResponse<?> addPost(Long boardId, PostRegisterDTO postRegisterDTO) throws Throwable {;
        postService.createPost(boardId, postRegisterDTO);
        return new HttpResponse<>(
                PrintResultMessage.POST_CREATE_SUCCESS,
                ResponseCode.CREATED,
                null
        );
    }

    @DeleteMapping(path = "/posts/remove")
    public HttpResponse<?> removePost(Long boardId, Long postId) throws Throwable {
        postService.deletePost(boardId, postId);
        return new HttpResponse<>(
                PrintResultMessage.POST_DELETE_SUCCESS,
                ResponseCode.ACCEPT,
                null
        );
    }

    @PutMapping(path = "/posts/edit")
    public HttpResponse<?> editPost(Long boardId, Long postId, PostUpdateDTO postUpdateDTO) throws Throwable {
        postService.updatePost(boardId, postId, postUpdateDTO);
        return new HttpResponse<>(
                PrintResultMessage.POST_UPDATE_SUCCESS,
                ResponseCode.ACCEPT,
                null
        );
    }

    @GetMapping(path = "/posts/view")
    public HttpResponse<?> viewPost(Long boardId, Long postId) {

        Post post = postService.getPost(postId, boardId);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("게시글 번호", post.getPostId());
        responseBody.put("작성일", post.getCreatedDate());
        if (post.getUpdatedDate() != null) {
            responseBody.put("수정일", post.getUpdatedDate());
        } else {
            responseBody.put("수정일", "수정 내역이 없습니다.");
        }
        responseBody.put("제목", post.getPostName());
        responseBody.put("내용", post.getPostContent());

        return new HttpResponse<>(
                ResponseCode.SUCCESS.getMessage(), ResponseCode.SUCCESS, responseBody
        );

    }
}
