package com.gokul.authservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.jni.Address;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {


    private String displayNAme;
    private String profilePictureUrl;
    private Date birthday;
    private Set<Address> addresses;
}
