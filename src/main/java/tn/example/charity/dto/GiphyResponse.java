package tn.example.charity.dto;

import java.util.List;

public class GiphyResponse {
    private List<GiphyData> data;

    public List<GiphyData> getData() {
        return data;
    }

    public void setData(List<GiphyData> data) {
        this.data = data;
    }

    public static class GiphyData {
        private GiphyImages images;

        public GiphyImages getImages() {
            return images;
        }

        public void setImages(GiphyImages images) {
            this.images = images;
        }
    }

    public static class GiphyImages {
        private GiphyOriginal original;

        public GiphyOriginal getOriginal() {
            return original;
        }

        public void setOriginal(GiphyOriginal original) {
            this.original = original;
        }
    }

    public static class GiphyOriginal {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
