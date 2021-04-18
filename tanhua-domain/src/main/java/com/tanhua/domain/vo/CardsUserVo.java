package com.tanhua.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardsUserVo implements Serializable {
    private Integer id;
    private String avatar;
    private String nickname;
    private String gender;
    private Integer age;
    private String[] tags;
}
