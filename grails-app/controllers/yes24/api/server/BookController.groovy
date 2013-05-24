package yes24.api.server

import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.TEXT

class BookController {

    def info() {
        def bookId = params.id
        def mainHttp = new HTTPBuilder('http://www.yes24.com/24/Goods/' + bookId)
        def mainResponse;
        mainHttp.get(contentType: TEXT) { resp, reader ->
            mainResponse = reader.text
        }

        def rankHttp = new HTTPBuilder('http://www.yes24.com/24/addModules/bestsellerrank/' + bookId + '/?categoryNumber=001001003016001012')
        def rankResponse;
        rankHttp.get(contentType: TEXT) { resp, reader ->
            rankResponse = reader.text
        }

        def salesPointMatcher = mainResponse =~ /판매지수 [0-9]* /
        def titleMatcher = mainResponse =~ /<title>.*<\/title>/
        def computerRankMatcher = rankResponse =~ /컴퓨터와 인터넷 [0-9]*위/

        render(contentType: "text/json") {
            title = titleMatcher[0].encodeAsHTML().replace("&lt;title&gt;", "").replace("&lt;/title&gt;", "")
            salesPoint = salesPointMatcher[0]
            rank = computerRankMatcher[0]
        }
    }
}
