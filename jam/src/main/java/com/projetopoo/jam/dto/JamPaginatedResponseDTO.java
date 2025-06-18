package com.projetopoo.jam.dto;

import java.util.List;

public class JamPaginatedResponseDTO {
    private List<JamSummaryDTO> jams;
    private long total;

    public JamPaginatedResponseDTO(List<JamSummaryDTO> jams, long total) {
        this.jams = jams;
        this.total = total;
    }

    public List<JamSummaryDTO> getJams() {
        return jams;
    }

    public void setJams(List<JamSummaryDTO> jams) {
        this.jams = jams;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
