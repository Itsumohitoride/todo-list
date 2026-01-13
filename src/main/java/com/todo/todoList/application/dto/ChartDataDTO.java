package com.todo.todoList.application.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

/**
 * Generic Chart Data DTO compatible with Chart.js format
 * Can be used for line charts, bar charts, etc.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Chart data in Chart.js format")
public class ChartDataDTO {
    @Schema(description = "Chart labels (X-axis)", example = "[\"Monday\", \"Tuesday\", \"Wednesday\"]")
    private List<String> labels;

    @Schema(description = "Datasets for the chart")
    private List<DatasetDTO> datasets;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Dataset for chart")
    public static class DatasetDTO {
        @Schema(description = "Dataset label", example = "Completed Tasks")
        private String label;

        @Schema(description = "Data points (Y-axis)", example = "[5, 10, 15]")
        private List<Integer> data;

        @Schema(description = "Background color", example = "#4CAF50")
        private String backgroundColor;

        @Schema(description = "Border color", example = "#45A049")
        private String borderColor;

        @Schema(description = "Border width", example = "2")
        private Integer borderWidth;
    }
}
