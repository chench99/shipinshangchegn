package org.example.springboot.DTO.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 收藏切换命令DTO
 * @author system
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "收藏切换命令DTO")
public class FavoriteToggleCommandDTO {

    @Schema(description = "零食ID", required = true)
    @NotNull(message = "零食ID不能为空")
    private Long snackId;
}
