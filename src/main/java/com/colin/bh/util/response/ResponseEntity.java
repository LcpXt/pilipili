package com.colin.bh.util.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2024年05月17日17:56
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseEntity<T> {

    private Status status;

    private String message;

    private T data;

}
