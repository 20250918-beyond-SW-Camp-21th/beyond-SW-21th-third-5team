import { http } from '../http'

export interface SaveOutfitRequest {
    date: string            // yyyy-MM-dd
    weatherNum: number      // PTY
    temperature: number    // TMX
    imageUrl: string
    outfitReview: string
    satisfaction: 'COLD' | 'GOOD' | 'HOT'
}

export const saveOutfit = (data: SaveOutfitRequest) => {
    return http.post('/outfit', data)
}