//>>built
define("dojox/xmpp/widget/ChatSession",["dojo","dijit","dojox","dojo/require!dijit/layout/LayoutContainer,dijit/_Templated"],function(a,b,c){a.provide("dojox.xmpp.widget.ChatSession");a.require("dijit.layout.LayoutContainer");a.require("dijit._Templated");a.declare("dojox.xmpp.widget.ChatSession",[b.layout.LayoutContainer,b._Templated],{templateString:a.cache("dojox.xmpp.widget","templates/ChatSession.html",'\x3cdiv\x3e\r\n\x3cdiv dojoAttachPoint\x3d"messages" dojoType\x3d"dijit.layout.ContentPane" layoutAlign\x3d"client" style\x3d"overflow:auto"\x3e\r\n\x3c/div\x3e\r\n\x3cdiv dojoType\x3d"dijit.layout.ContentPane" layoutAlign\x3d"bottom" style\x3d"border-top: 2px solid #333333; height: 35px;"\x3e\x3cinput dojoAttachPoint\x3d"chatInput" dojoAttachEvent\x3d"onkeypress: onKeyPress" style\x3d"width: 100%;height: 35px;" /\x3e\x3c/div\x3e\r\n\x3c/div\x3e'),
enableSubWidgets:!0,widgetsInTemplate:!0,widgetType:"ChatSession",chatWith:null,instance:null,postCreate:function(){},displayMessage:function(a,b){a&&(this.messages.domNode.innerHTML+="\x3cb\x3e"+(a.from?this.chatWith:"me")+":\x3c/b\x3e "+a.body+"\x3cbr/\x3e",this.goToLastMessage())},goToLastMessage:function(){this.messages.domNode.scrollTop=this.messages.domNode.scrollHeight},onKeyPress:function(b){(b.keyCode||b.charCode)==a.keys.ENTER&&""!=this.chatInput.value&&(this.instance.sendMessage({body:this.chatInput.value}),
this.displayMessage({body:this.chatInput.value},"out"),this.chatInput.value="")}})});