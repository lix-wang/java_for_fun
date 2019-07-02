package com.xiao.marshaller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo of marshaller.
 *
 * @author lix wang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    private String author;
    private String version;
    private int authorAmount;
}
