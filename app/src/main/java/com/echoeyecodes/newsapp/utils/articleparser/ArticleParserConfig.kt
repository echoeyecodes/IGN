package com.echoeyecodes.newsapp.utils.articleparser

/**
 * Class used to configure behaviour for parsing html content
 *
 * @param breakDepth is used to determine the point at which a new block or article
 * paragraph content  is to be added to the article array
 */

class ArticleParserConfig(var breakDepth:Int = 0){

    /**
     * use this variable to control traversal
     */
    var shouldPause = false
}