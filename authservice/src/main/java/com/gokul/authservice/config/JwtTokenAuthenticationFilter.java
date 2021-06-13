package com.gokul.authservice.config;

import com.gokul.authservice.model.InstaUserDetails;
import com.gokul.authservice.service.JwtTokenProvider;
import com.gokul.authservice.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtConfig jwtConfig;
    private JwtTokenProvider tokenProvider;
    private UserService userService;
    private String serviceUsername;

    public JwtTokenAuthenticationFilter(JwtConfig jwtConfig, JwtTokenProvider tokenProvider, UserService userService, String serviceUsername) {
        this.jwtConfig = jwtConfig;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.serviceUsername = serviceUsername;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {


//        getting the authentication header from the

        String header=request.getHeader(jwtConfig.getHeader());

//        validate header
        if(header==null|| !header.startsWith(jwtConfig.getPrefix())){
            chain.doFilter(request,response);
            return;
        }

        String token=header.replace(jwtConfig.getPrefix(),"");

        if(tokenProvider.validateToken(token)){

            Claims claims=tokenProvider.getClaimsFromJWT(token);
            String username=claims.getSubject();

            UsernamePasswordAuthenticationToken auth=null;


            if(username.equals(serviceUsername)){

                List<String> authorities=(List<String>)claims.get("authorities");

                auth=new UsernamePasswordAuthenticationToken(username,null,authorities.stream().map(SimpleGrantedAuthority::new).collect(toList()));
            }else{
                auth = userService
                        .findByUsername(username)
                        .map(InstaUserDetails::new)
                        .map(userDetails -> {

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                            authentication
                                    .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                            return authentication;
                        })
                        .orElse(null);
            }
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        else{
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request,response);
    }
}
