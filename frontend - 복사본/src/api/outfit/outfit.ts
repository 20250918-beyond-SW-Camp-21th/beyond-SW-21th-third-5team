import type { AxiosResponse } from "axios";
import { http } from "../http";

export type OutfitSatisfaction = "COLD" | "GOOD" | "HOT"; // ✅ 백엔드 enum과 반드시 일치

export interface OutFitListItemResponse {
    id: number;
    saveDate: string;       // yyyy-MM-dd (백엔드가 saveDate로 내려준다고 가정)
    imageUrl: string | null;
}

export interface OutFitPageResponse {
    items: OutFitListItemResponse[];
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    hasPrevious: boolean;
    hasNext: boolean;
}

export interface OutFitDetailResponse {
    id: number;
    saveDate: string;
    weatherNum: number;
    temperature: number;
    imageUrl: string;
    outfitReview: string;
    satisfaction: OutfitSatisfaction;
}

export interface SaveOutfitPayload {
    weatherNum: number;
    temperature: number;
    outfitReview: string;
    date: string; // yyyy-MM-dd
    satisfaction: OutfitSatisfaction;
    image: File;
}

function toSaveOutfitFormData(p: SaveOutfitPayload): FormData {
    const fd = new FormData();
    fd.append("weatherNum", String(p.weatherNum));
    fd.append("temperature", String(p.temperature));
    fd.append("outfitReview", p.outfitReview);
    fd.append("date", p.date);
    fd.append("satisfaction", p.satisfaction);
    fd.append("image", p.image);
    return fd;
}

export const outfitApi = {
    getOutfits(page = 0, size = 16): Promise<AxiosResponse<OutFitPageResponse>> {
        return http.get("/outfit", { params: { page, size } });
    },

    getMyOutfitDetail(id: number): Promise<AxiosResponse<OutFitDetailResponse>> {
        return http.get(`/outfit/${id}`);
    },

    saveOutfit(payload: SaveOutfitPayload): Promise<AxiosResponse<void>> {
        // ✅ Content-Type을 굳이 지정하지 않습니다 (axios가 boundary 포함해 설정)
        const fd = toSaveOutfitFormData(payload);
        return http.post("/outfit", fd);
    },
};