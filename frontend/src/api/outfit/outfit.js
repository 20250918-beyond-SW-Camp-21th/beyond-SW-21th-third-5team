import { http } from '../http'

export const outfitApi = {
    getOutfits(page = 0, size = 16){
        return http.get("/api/outfit", {
            params: { page, size }
        });
    },

    getMyOutfitDetail(id) {
        return http.get(`/api/outfit/${id}`);
    },

    saveOutfit(formData) {
        return http.post("/api/outfit", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
        });
    }
};