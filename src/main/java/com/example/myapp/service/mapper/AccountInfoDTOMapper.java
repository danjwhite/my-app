package com.example.myapp.service.mapper;

import com.example.myapp.domain.User;
import com.example.myapp.dto.AccountInfoDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountInfoDTOMapper {

    public AccountInfoDTO mapToAccountInfoDTO(User user) {
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        accountInfoDTO.setUsername(user.getUsername());
        accountInfoDTO.setFirstName(user.getFirstName());
        accountInfoDTO.setLastName(user.getLastName());

        return accountInfoDTO;
    }

    public void mapToUser(AccountInfoDTO source, User target) {
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());

    }
}
