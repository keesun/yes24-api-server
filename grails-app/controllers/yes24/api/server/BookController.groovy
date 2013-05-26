package yes24.api.server

import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.TEXT

class BookController {

    def info() {
        def bookId = params.id
        if (bookId == null || bookId.size() == 0 ) {
            render(contentType: "text/json") {
                errorCode = '404'
                errorMsg = 'Arr... There is no bookId! What is bookId? ex) http://www.yes24.com/24/goods/8069226. 8069226 is bookId'
            }
            return
        }
        def mainHttp = new HTTPBuilder('http://www.yes24.com/24/Goods/' + bookId)
        def mainResponse
        mainHttp.get(contentType: TEXT) { resp, reader ->
            if(reader == null) {
                render(contentType: "text/json") {
                    errorCode = '404'
                    errorMsg = 'Sorry, that book doesn’t exist!'
                }
            }
            mainResponse = reader.text
        }

        def rankHttp = new HTTPBuilder('http://www.yes24.com/24/addModules/bestsellerrank/' + bookId + '/?categoryNumber=001001003016001012')
        def rankResponse = ""
        rankHttp.get(contentType: TEXT) { resp, reader ->
            if (reader != null) {
                rankResponse = reader.text
            }
        }

        def reviewHttp = new HTTPBuilder('http://www.yes24.com/24/communityModules/ReviewList/' + bookId);
        def reviewResponse = ""

        reviewHttp.get(contentType: TEXT) { resp, reader ->
            reviewResponse = reader.text
        }

        def salesPointMatcher = mainResponse =~ /판매지수 [0-9]*/
        def titleMatcher = mainResponse =~ /<title>.*<\/title>/
        def computerRankMatcher = rankResponse =~ /컴퓨터와 인터넷 [0-9]*위/
        def reviewMatcher = reviewResponse.replaceAll("\\r\\n", '') =~ /<h2 class="reviewTitle">.*>(\d+).*평균별점/

        render(contentType: "text/json") {
            title = titleMatcher[0].toString().replaceAll("\\<.*?\\>", "").replace("YES24 - ", "")
            salesPoint = salesPointMatcher[0]
            if (computerRankMatcher.find()) {
                rank = computerRankMatcher[0]
            }
            if (reviewMatcher.find()) {
                reviewCount = reviewMatcher[0][1]
            }
        }
    }
}
