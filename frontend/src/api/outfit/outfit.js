import { http } from '../http'

export const outfitApi = {
    getOutfits(page = 0, size = 16){
        return http.get("/outfit", {
            params: { page, size }
        });
    },

    getMyOutfitDetail(id) {
        return http.get(`/outfit/${id}`);
    },

    saveOutfit(formData) {
        return http.post("/outfit", formData, {
            headers: {
                "Content-Type": "application/json",
            },
        });
    }
};