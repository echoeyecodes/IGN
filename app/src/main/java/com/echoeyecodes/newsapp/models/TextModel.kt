package com.echoeyecodes.newsapp.models

open class RegularText(val text:String)

class ItalicText(text: String):RegularText(text)
class LinkText(text: String):RegularText(text)
class BoldText(text: String):RegularText(text)
class HeaderText(text: String):RegularText(text)