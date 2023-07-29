package com.myfin.api.dto;

import com.myfin.core.dto.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SearchAccount {
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private MetaResponse meta;
        private DocumentResponse document;

        public static Response fromDto(AccountDto dto) {
            return dto != null ?
                    Response.builder()
                            .meta(new MetaResponse(true))
                            .document(new DocumentResponse(dto.getOwner().getName()))
                            .build() :
                    Response.builder()
                            .meta(new MetaResponse(false))
                            .build();
        }

        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        private static class MetaResponse {
            private boolean result;
        }

        @Data @NoArgsConstructor @AllArgsConstructor @Builder
        private static class DocumentResponse {
            private String userName;
        }
    }
}
