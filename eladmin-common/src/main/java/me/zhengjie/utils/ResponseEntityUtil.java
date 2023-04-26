package me.zhengjie.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.Charset;

public class ResponseEntityUtil {
    public static ResponseEntity getSuccess(Object body){
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("text","html", Charset.forName("utf-8"));
        headers.setContentType(mediaType);
        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }

    public static ResponseEntity getSuccess(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public static ResponseEntity getFail(Object body){
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("text","html", Charset.forName("utf-8"));
        headers.setContentType(mediaType);
        return new ResponseEntity<>(body, headers, HttpStatus.BAD_REQUEST);
    }
}
