package app.x1co.newwebclaude

object WebShield {
    private val blockedDomains = setOf(
        // Sites adultos principais
        "pornhub.com", "xvideos.com", "xnxx.com", "redtube.com", "youporn.com",
        "porn.com", "tube8.com", "spankbang.com", "eporner.com", "xhamster.com",
        "chaturbate.com", "stripchat.com", "cam4.com", "myfreecams.com",
        "onlyfans.com", "fansly.com", "manyvids.com",

        // Palavras-chave comuns
        "xxx", "nsfw", "adult", "sex", "porn", "nude", "hentai",
        "camgirl", "webcam", "escort", "fetish", "erotic",

        // Sites de conteúdo +18
        "rule34.xxx", "e621.net", "nhentai.net", "hanime.tv",
        "hentaihaven.xxx", "fakku.net", "doujinshi.org",

        // Outros
        "4chan.org", "8kun.top", "gelbooru.com", "danbooru.donmai.us"
    )

    private val blockedKeywords = listOf(
        "porn", "xxx", "sex", "adult", "nsfw", "nude", "naked",
        "hentai", "camgirl", "webcam", "escort", "erotic", "fetish",
        "18+", "+18", "r34", "rule34", "doujin", "ecchi"
    )

    fun isBlocked(url: String): Boolean {
        val lowerUrl = url.lowercase()

        // Verifica domínios bloqueados
        for (domain in blockedDomains) {
            if (lowerUrl.contains(domain)) {
                return true
            }
        }

        // Verifica palavras-chave
        for (keyword in blockedKeywords) {
            if (lowerUrl.contains(keyword)) {
                return true
            }
        }

        return false
    }
}