package cn.lucasji.starry.idp.core.mapper;

import cn.lucasji.starry.idp.core.entity.User;
import cn.lucasji.starry.idp.infrastructure.dto.UserDto;
import cn.lucasji.starry.idp.infrastructure.dto.req.AddUserReq;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * @author Lucas Ji
 * @date 2024/1/31 9:34
 */
@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  User convertedFrom(AddUserReq addUserReq);

  UserDto convertToUserDto(User user);
}
