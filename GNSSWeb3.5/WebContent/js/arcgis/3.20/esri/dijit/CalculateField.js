// All material copyright ESRI, All Rights Reserved, unless otherwise specified.
// See http://js.arcgis.com/3.20/esri/copyright.txt for details.
//>>built
require({cache:{"url:esri/dijit/templates/CalculateField.html":'\x3cdiv class\x3d"${_css.base}"\x3e\r\n  \x3cdiv class\x3d"esriFloatLeading esriCalcHelp" data-dojo-attach-point\x3d"_helpNode"\x3e\r\n    \x3ca href\x3d"#" class\x3d""\x3e${i18n.learnMore}\x3c/a\x3e\r\n  \x3c/div\x3e\r\n  \x3cdiv data-dojo-type\x3d"dijit/layout/ContentPane" style\x3d"width:99%;margin-top:0.5em; margin-bottom: 0.5em;"\x3e\r\n    \x3cdiv data-dojo-attach-point\x3d"_header" class\x3d"${_css.titleLabel}"\x3e\r\n       \x3cdiv class\x3d"esriAlignLeading"\x3e${i18n.expBuilderTitle}\x3c/div\x3e\r\n    \x3c/div\x3e\r\n    \x3cdiv style\x3d"clear:both; border-bottom: #CCC thin solid;height:4%;max-height:4em;width:100%;"\x3e\x3c/div\x3e\r\n  \x3c/div\x3e  \r\n  \x3cdiv data-dojo-type\x3d"dijit/form/Form" data-dojo-attach-point\x3d"_expressionForm" style\x3d"height:100%;width:99%;"\x3e\r\n    \x3cdiv class\x3d"esriFloatLeading" data-dojo-attach-point\x3d"_selCalcFieldDiv" style\x3d"width:100%;padding-bottom:0.5em;"\x3e\r\n      \x3cdiv\x3e\x3clabel class\x3d"esriLeadingMargin1"\x3e${i18n.selectCalField}\x3c/label\x3e\x3c/div\x3e\r\n      \x3cselect class\x3d"esriLeadingMargin1 ${_css.selectField}" data-dojo-type\x3d"dijit/form/Select" data-dojo-attach-point\x3d"_selCalcField"\x3e\x3c/select\x3e\r\n    \x3c/div\x3e\r\n    \x3cdiv style\x3d"width:100%;padding-bottom:0.5em;" class\x3d"esriFloatLeading"\x3e\r\n      \x3clabel class\x3d"esriLeadingMargin1" data-dojo-attach-point\x3d"_calcFieldLabel"\x3e\x3c/label\x3e\r\n    \x3c/div\x3e\r\n    \x3cinput class\x3d"esriFloatLeading esriLeadingMargin1 ${_css.expressionBox}" dir\x3d"ltr" data-dojo-type\x3d"dijit/form/SimpleTextarea" data-dojo-attach-point\x3d"_exprBox" data-dojo-props\x3d"rows:4 ,cols:2,required:true,intermediateChanges:true"\x3e\x3c/input\x3e\r\n    \x3cdiv class\x3d"esriFloatLeading ${_css.actionBtnContainer}"\x3e\r\n      \x3cdiv class\x3d"esriFloatLeading esriLeadingMargin1" style\x3d"width:70%;padding-bottom:0.5em;" dir\x3d"ltr" data-dojo-attach-point\x3d"_operatorCtr"\x3e\x3c/div\x3e\r\n      \x3cdiv data-dojo-type\x3d"dijit/form/Button" class\x3d"${_css.actionButton} esriFloatTrailing esriTrailingMargin1" data-dojo-props\x3d"label:\'${i18n.remove}\',iconClass:\'${_css.clearIcon}\',showLabel: false, disabled:true" data-dojo-attach-event\x3d"onClick:_handleRemoveBtnClick" data-dojo-attach-point\x3d"_removeBtn"\x3e\x3c/div\x3e\r\n      \x3cdiv data-dojo-type\x3d"dijit/form/Button" class\x3d"${_css.actionButton} esriFloatTrailing esriTrailingMargin1" data-dojo-props\x3d"label:\'${i18n.validate}\',iconClass:\'${_css.validateIcon}\',showLabel: false, disabled:true" data-dojo-attach-event\x3d"onClick:_handleValidationBtnClick" data-dojo-attach-point\x3d"_validateBtn"\x3e\x3c/div\x3e\r\n    \x3c/div\x3e\r\n    \x3cdiv class\x3d"${_css.fieldFunctionContainer}"\x3e\r\n      \x3cdiv class\x3d"esriFloatLeading ${_css.fieldContainer}"\x3e\r\n        \x3cdiv class\x3d"${_css.fieldLabelDiv}"\x3e\x3clabel class\x3d"esriLeadingMargin1 ${_css.titleLabel}"\x3e${i18n.fields}\x3c/label\x3e\x3c/div\x3e\r\n        \x3cdiv class\x3d"${_css.fieldTypeContainer}"\x3e\r\n          \x3clabel class\x3d"esriLeadingMargin1 esriSelectLabel esriTrailingMargin05"\x3e\r\n            \x3cinput type\x3d"radio" data-dojo-type\x3d"dijit/form/RadioButton" data-dojo-attach-point\x3d"_strRadioBtn" data-dojo-props\x3d"\'class\':\'esriSelectLabel\'" name\x3d"functionType" value\x3d"StrType"/\x3e\r\n            ${i18n.stringLabel}\r\n          \x3c/label\x3e\r\n          \x3clabel class\x3d"esriSelectLabel esriTrailingMargin05"\x3e\r\n            \x3cinput type\x3d"radio" data-dojo-type\x3d"dijit/form/RadioButton" data-dojo-attach-point\x3d"_numRadioBtn" data-dojo-props\x3d"\'class\':\'esriSelectLabel\'" name\x3d"functionType" value\x3d"NumType"/\x3e \r\n            ${i18n.numeric}\r\n          \x3c/label\x3e\r\n          \x3clabel class\x3d"esriSelectLabel"\x3e\r\n            \x3cinput type\x3d"radio" data-dojo-type\x3d"dijit/form/RadioButton" data-dojo-attach-point\x3d"_dateRadioBtn"  data-dojo-props\x3d"\'class\':\'esriSelectLabel\'" name\x3d"functionType" value\x3d"DateType"/\x3e\r\n            ${i18n.dateLabel}\r\n          \x3c/label\x3e\r\n        \x3c/div\x3e\r\n        \x3cdiv class\x3d"esriLeadingMargin1 ${_css.fieldListContainer}" dir\x3d"ltr" data-dojo-attach-point\x3d"_attributeListCtr"\x3e\r\n        \x3c/div\x3e\r\n      \x3c/div\x3e\r\n      \x3cdiv class\x3d"esriFloatTrailing ${_css.functionContainer}"\x3e\r\n        \x3cdiv class\x3d"${_css.functionLabelDiv}"\x3e\r\n          \x3clabel class\x3d"${_css.titleLabel}"\x3e${i18n.functions}\x3c/label\x3e\r\n        \x3c/div\x3e\r\n        \x3cdiv data-dojo-attach-point\x3d"_helpersListCtr" dir\x3d"ltr" class\x3d"${_css.functionListContainer}"\x3e\r\n        \x3c/div\x3e\r\n      \x3c/div\x3e\r\n    \x3c/div\x3e\r\n    \x3cdiv class\x3d"esriLeadingMargin05 ${_css.formWarning} esriRoundedBox" data-dojo-attach-point\x3d"_errorMessagePane" style\x3d"clear:both;display:none;"\x3e\r\n      \x3ca href\x3d"#" title\x3d"${i18n.close}" class\x3d"esriFloatTrailing esri-icon-close" title\x3d\'${i18n.close}\' data-dojo-attach-event\x3d"onclick:_handleCloseMsg"\x3e\r\n      \x3c/a\x3e\r\n      \x3cspan data-dojo-attach-point\x3d"_bodyNode" style\x3d"width:100%;height:100%;"\x3e\x3c/span\x3e\r\n    \x3c/div\x3e\r\n    \x3cdiv style\x3d"clear:both; border-bottom: #CCC thin solid; height:1px;width:100%;"\x3e\x3c/div\x3e\r\n    \x3cdiv  class\x3d"esriFloatTrailing esriTrailingMargin2" data-dojo-attach-point\x3d"_buttonCtr" style\x3d"padding:1em 0"\x3e\r\n      \x3cdiv data-dojo-type\x3d"dijit/form/Button"  class\x3d"${_css.addButton}" data-dojo-attach-point\x3d"_addBtn" data-dojo-attach-event\x3d"onClick:_handleAddButtonClick"\x3e\r\n        ${calculateLabel}\r\n      \x3c/div\x3e\r\n       \x3cdiv data-dojo-type\x3d"dijit/form/Button" class\x3d"esriLeadingMargin05 ${_css.closeButton}" data-dojo-attach-point\x3d"_closeBtn" data-dojo-attach-event\x3d"onClick:_handleCloseButtonClick"\x3e\r\n        ${i18n.close}\r\n      \x3c/div\x3e\r\n    \x3c/div\x3e\r\n  \x3c/div\x3e\r\n  \x3cdiv class\x3d"dijitDialogUnderlayWrapper" data-dojo-attach-point\x3d"_underlay" style\x3d"position:absolute;z-index:949;display:none;top:0px;left:0px;width:100%;height:100%;"\x3e\r\n    \x3cdiv class\x3d"dijitDialogUnderlay" tabindex\x3d"-1" style\x3d"width:100%;height:100%"\x3e\x3c/div\x3e\r\n    \x3cdiv class\x3d"${_css.loadingIcon}" style\x3d"height:100%;"\x3e\x3c/div\x3e\r\n  \x3c/div\x3e\r\n\x3c/div\x3e  \r\n'}});
define("esri/dijit/CalculateField","dojo/_base/declare dojo/_base/lang dojo/_base/array dojo/_base/connect dojo/_base/json dojo/_base/kernel dojo/_base/fx dojo/has dojo/json dojo/string dojo/dom-style dojo/dom-attr dojo/dom-construct dojo/query dojo/dom-class dojo/_base/event dojo/Evented dojo/fx/easing dojo/store/Memory dojo/mouse dojo/on dojo/_base/window dijit/_WidgetBase dijit/_TemplatedMixin dijit/_WidgetsInTemplateMixin dijit/_OnDijitClickMixin dijit/_FocusMixin dijit/registry dijit/form/Button dijit/form/CheckBox dijit/form/Form dijit/form/Select dijit/form/MultiSelect dijit/form/TextBox dijit/form/SimpleTextarea dijit/form/ValidationTextBox dijit/layout/ContentPane dijit/form/ComboBox dijit/Dialog dijit/Tooltip dgrid/OnDemandList dgrid/Selection dgrid/Keyboard dgrid/extensions/DijitRegistry dgrid/util/mouse put-selector/put ../kernel ../lang ../request ./SingleFilter dojo/i18n!../nls/jsapi dojo/text!./templates/CalculateField.html".split(" "),
function(n,d,f,V,x,p,y,I,W,e,h,t,k,X,z,A,J,B,u,C,D,E,K,L,M,N,O,Y,P,Z,aa,ba,ca,da,ea,fa,ga,ha,ia,v,Q,R,S,T,q,ja,r,w,F,ka,l,U){var G=n([Q,S,R,T]),H={base:"esriCalcField",titleLabel:"esriCalcTitleLabel",selectField:"esriCalcSelField",expressionBox:"esriExprBox",actionBtnContainer:"esriActionButtonCtr",validateIcon:"esriCalcFieldValidateIcon",validateDisabledIcon:"esriCalcFieldValidateDisabledIcon",actionButton:"esriActionButton",clearIcon:"esriCalcFieldClearIcon",clearDisabledIcon:"esriCalcFieldClearDisabledIcon",
fieldFunctionContainer:"esriCalcFieldFuncCtr",fieldContainer:"esriCalcFieldCtr",functionContainer:"esriCalcFuncCtr",fieldLabelDiv:"esriFieldsLabelDiv",functionLabelDiv:"esriFunctionLabelDiv",fieldListContainer:"esriCalcFieldsList",functionListContainer:"esriCalcFunctionList",fieldTypeContainer:"esriFieldsSelectionCtr",loadingIcon:"esriLoadingLarge",formWarning:"esriFormWarning",formSuccess:"esriFormSuccess",addButton:"",closeButton:""};n=n([K,L,M,N,O,J],{declaredClass:"esri.dijit.CalculateField",
templateString:U,widgetsInTemplate:!0,showSelectField:!1,showHeader:!0,closeOnAdd:!0,addButtonClass:"",closeButtonClass:"",_showMsgTimerInterval:3E3,showHelp:!1,baseClass:H.base,constructor:function(a){a.containerNode&&(this.container=a.containerNode);this._css=d.mixin(H,a.css)},destroy:function(){this.inherited(arguments)},postMixInProperties:function(){this.inherited(arguments);this.i18n={};d.mixin(this.i18n,l.common);d.mixin(this.i18n,l.calculateFields);this.calculateLabel||(this.calculateLabel=
this.i18n.calculate)},postCreate:function(){this.inherited(arguments);this._buildUI();this._loadEvents();var a=["ar","he"],b,c;this.onlineHelpMap={};for(b=0;b<a.length;b+=1)c=a[b],p.locale&&-1!==p.locale.indexOf(c)&&(-1!==p.locale.indexOf("-")?-1!==p.locale.indexOf(c+"-")&&(this._isRightToLeft=!0):this._isRightToLeft=!0);h.set(this._helpNode,"display",this.showHelp?"block":"none");this.validate()},_buildUI:function(){var a=[],b;h.set(this._header,"display",this.showHeader?"block":"none");h.set(this._selCalcFieldDiv,
"display",this.showSelectField?"block":"none");this.field&&(b=f.filter(this.layer.fields,function(a){return a.name===this.field},this),this._calcField=b=b[0],t.set(this._calcFieldLabel,"innerHTML",e.substitute(this.i18n.exprLabel,{fieldName:b?b.name:this.field})));if(!this.helperMethods||this.helperMethods&&0===this.helperMethods.length)b=[{type:"NumType",label:e.substitute(this.i18n.absFunc,{functionName:"ABS(\x3ci\x3enumber\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"ABS()"},{type:"NumType",
label:e.substitute(this.i18n.castFunc,{functionName:"CAST(\x3ci\x3enumber\x3c/i\x3e",num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"CAST()"},{type:"NumType",label:e.substitute(this.i18n.ceilingFunc,{functionName:"CEILING(\x3ci\x3enumber\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"CEILING()"},{type:"NumType",label:e.substitute(this.i18n.cosFunc,{functionName:"COS(\x3ci\x3enumber\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"COS()"},{type:"NumType",label:e.substitute(this.i18n.floorFunc,{functionName:"FLOOR(\x3ci\x3enumber\x3c/i\x3e)",
num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"FLOOR()"},{type:"NumType",label:e.substitute(this.i18n.logFunc,{functionName:"LOG(\x3ci\x3enumber\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"LOG()"},{type:"NumType",label:e.substitute(this.i18n.log10Func,{functionName:"LOG10(\x3ci\x3enumber\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"LOG10()"},{type:"NumType",label:e.substitute(this.i18n.modFunc,{functionName:"MOD(\x3ci\x3enumber\x3c/i\x3e, \x3ci\x3en\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e",
n:"\x3ci\x3en\x3c/i\x3e"}),name:"MOD(,)"},{type:"NumType",label:e.substitute(this.i18n.powerFunc,{functionName:"POWER(\x3ci\x3enumber\x3c/i\x3e, \x3ci\x3ey\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e",y:"\x3ci\x3ey\x3c/i\x3e"}),name:"POWER(,)"},{type:"NumType",label:e.substitute(this.i18n.roundFunc,{functionName:"ROUND(\x3ci\x3enumber\x3c/i\x3e, \x3ci\x3elength\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e",length:"\x3ci\x3elength\x3c/i\x3e"}),name:"ROUND(,)"},{type:"NumType",label:e.substitute(this.i18n.sinFunc,
{functionName:"SIN(\x3ci\x3enumber\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"SIN()"},{type:"NumType",label:e.substitute(this.i18n.tanFunc,{functionName:"TAN(\x3ci\x3enumber\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e"}),name:"TAN()"},{type:"NumType",label:e.substitute(this.i18n.truncateFunc,{functionName:"TRUNCATE(\x3ci\x3enumber\x3c/i\x3e, \x3ci\x3edecimal_place\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e",decimal_place:"\x3ci\x3edecimal_place\x3c/i\x3e"}),name:"TRUNCATE(,)"},{type:"NumType",
label:e.substitute(this.i18n.nullifFunc,{functionName:"NULLIF(\x3ci\x3enumber\x3c/i\x3e,\x3ci\x3evalue\x3c/i\x3e)",num:"\x3ci\x3enumber\x3c/i\x3e",value:"\x3ci\x3evalue\x3c/i\x3e"}),name:"NULLIF(,)"},{type:"StrType",label:e.substitute(this.i18n.char_lengthFunc,{functionName:"CHAR_LENGTH(\x3ci\x3estring\x3c/i\x3e)",str:"\x3ci\x3estring\x3c/i\x3e"}),name:"CHAR_LENGTH()"},{type:"StrType",label:e.substitute(this.i18n.concatFunc,{functionName:"CONCAT(\x3ci\x3estring1\x3c/i\x3e, \x3ci\x3estring2\x3c/i\x3e)"}),
name:"CONCAT(,)"},{type:"StrType",label:e.substitute(this.i18n.positionFunc,{functionName:"POSITION(\x3ci\x3esubstring\x3c/i\x3e, \x3ci\x3estring\x3c/i\x3e)",str:"\x3ci\x3estring\x3c/i\x3e"}),name:"POSITION(,)"},{type:"StrType",label:e.substitute(this.i18n.lowerFunc,{functionName:"LOWER(\x3ci\x3estring\x3c/i\x3e)",str:"\x3ci\x3estring\x3c/i\x3e"}),name:"LOWER()"},{type:"StrType",label:e.substitute(this.i18n.substringFunc,{functionName:"SUBSTRING(\x3ci\x3estring\x3c/i\x3e, \x3ci\x3estart\x3c/i\x3e, \x3ci\x3elength\x3c/i\x3e)",
start:"\x3ci\x3estart\x3c/i\x3e",length:"\x3ci\x3elength\x3c/i\x3e",str:"\x3ci\x3estring\x3c/i\x3e"}),name:"SUBSTRING(,,)"},{type:"StrType",label:e.substitute(this.i18n.trimFunc,{functionName:"TRIM(BOTH|LEADING|TRAILING] \u2018 \u2018 FROM expression)",str:"\x3ci\x3estring\x3c/i\x3e"}),name:"TRIM()"},{type:"StrType",label:e.substitute(this.i18n.upperFunc,{functionName:"UPPER(\x3ci\x3estring\x3c/i\x3e)",str:"\x3ci\x3estring\x3c/i\x3e"}),name:"UPPER()"},{type:"DateType",label:e.substitute(this.i18n.current_dateFunc,
{functionName:"CURRENT_DATE()"}),name:"CURRENT_DATE()"},{type:"DateType",label:e.substitute(this.i18n.current_timeFunc,{functionName:"CURRENT_TIME()"}),name:"CURRENT_TIME()"},{type:"DateType",label:e.substitute(this.i18n.current_timestampFunc,{functionName:"CURRENT_TIMESTAMP()"}),name:"CURRENT_TIMESTAMP()"},{type:"NumType",label:e.substitute(this.i18n.extractFunc,{functionName:"EXTRACT(unit FROM date)"}),name:"EXTRACT()"}],f.forEach(b,function(a){a.label="\x3cb\x3e"+a.label.substring(0,a.label.lastIndexOf(":")+
1)+"\x3c/b\x3e\x3cbr/\x3e "+a.label.substring(a.label.lastIndexOf(":")+1)},this),this.set("helperMethods",b);(!this.operators||this.operators&&0===this.operators.length)&&this.set("operators","+-/*()".split(""));this._operatorBtns=[];f.forEach(this.operators,function(a){this._operatorBtns.push(new P({value:a,label:a,style:{width:"4em"},onClick:d.hitch(this,this._updateExpression,{value:a,type:"operator"})},k.create("div",null,this._operatorCtr)))},this);this.layer&&this.layer.fields&&0<this.layer.fields.length&&
(a=this._createIds(this.layer.fields),b=f.map(this.layer.fields,function(a){return{label:a.name,value:a.name}}),this._selCalcField.addOption(b),this._selCalcField.set("value",this.field));this.fieldsStore=new u({data:a});this.attributeList=new G({renderRow:d.hitch(this,this._renderAttributesRow),selectionMode:"single",store:this.fieldsStore},this._attributeListCtr);a=this._createIds(this.get("helperMethods"));this.operatorStore=new u({data:a});this.helpersList=new G({renderRow:d.hitch(this,this._renderOperatorRow),
selectionMode:"single",store:this.operatorStore},this._helpersListCtr)},_loadEvents:function(){this.watch("fields",d.hitch(this,this._handleFieldsChange));this.watch("field",d.hitch(this,this._handleFieldChange));if(this.showSelectField)this._selCalcField.on("change",d.hitch(this,this._handleSelcCalFieldChange));this._expressionForm.watch("value",d.hitch(this,this._handleHelperTypeChange));this._expressionForm.on("focus",d.hitch(this,this._setfocus));this._exprBox.watch("value",d.hitch(this,this._handleExpChange));
this.attributeList.on("dgrid-select",d.hitch(this,function(a){this._updateExpression({value:a.rows[0].data,type:"field"})}));this.helpersList.on("dgrid-select",d.hitch(this,function(a){this._updateExpression({value:a.rows[0].data,type:"helper"})}));this.attributeList.on(q.enterRow,d.hitch(this,function(a){a=this.attributeList.row(a);var b,c;b=a.data.alias||a.data.name;c="";c=this._getTypeLabel(a.data.type);this._showTooltip(a.element,"\x3cb\x3e"+b+"\x3c/b\x3e: "+c)}));this.attributeList.on(q.leaveRow,
d.hitch(this,function(a){a=this.attributeList.row(a);this._hideTooltip(a.element)}));this.helpersList.on(q.enterRow,d.hitch(this,function(a){a=this.helpersList.row(a);this._showTooltip(a.element,a.data.label)}));this.helpersList.on(q.leaveRow,d.hitch(this,function(a){a=this.helpersList.row(a);this._hideTooltip(a.element)}));this.attributeList.on("dgrid-refresh-complete",d.hitch(this,this._setfocus));this.helpersList.on("dgrid-refresh-complete",d.hitch(this,this._setfocus));this._exprBox.on("blur",
d.hitch(this,function(){this._exprBox.textbox.setSelectionRange&&"number"==typeof this._exprBox.textbox.selectionStart?this._exprBox.set("cursorPosition",[this._exprBox.textbox.selectionStart,this._exprBox.textbox.selectionEnd]):this._exprBox.set("cursorPosition",this._getCursorRange(this._exprBox.textbox))}));this._exprBox.on("focus",d.hitch(this,function(){var a=this._exprBox.get("cursorPosition");a&&(this._exprBox.textbox.setSelectionRange&&"number"==typeof this._exprBox.textbox.selectionStart?
this._exprBox.textbox.setSelectionRange(a[1],a[1]):this._setCaretPosition(this._exprBox.textbox,a[1],a[1]))}));D(this._calcFieldLabel,C.enter,d.hitch(this,function(a){a="";a=this._getTypeLabel(this._calcField.type);this._showTooltip(this._calcFieldLabel,"\x3cb\x3e"+this._calcField.alias+"\x3c/b\x3e: "+a)}));D(this._calcFieldLabel,C.leave,d.hitch(this,function(a){this._hideTooltip(this._calcFieldLabel)}))},startup:function(){this.inherited(arguments);this.attributeList.startup();this.helpersList.startup();
this.set("helperType",this.helperType)},reset:function(){r.show(this.domNode);this._expressionForm.reset();this._handleCloseMsg();this.set("helperType",this.helperType)},_close:function(){this.emit("close",{});r.hide(this.domNode)},_createIds:function(a){var b=[];a&&0<a.length&&(b=f.map(a,function(a,b){return d.mixin(a,{id:b})}));return b},_renderAttributesRow:function(a){var b=k.create("div",{"class":"esriCalExpRowOuter"}),c=k.create("div",{"class":"esriCalcExpLabelRow"},b);k.create("div",{"class":"esriCalcFieldTextTrimWithEllipses",
innerHTML:a.name},c);return b},_renderOperatorRow:function(a){var b=k.create("div",{"class":"esriCalExpRowOuter"}),c=k.create("div",{"class":"esriCalcExpLabelRow"},b);k.create("div",{"class":"esriCalcFieldTextTrimWithEllipses",innerHTML:a.name},c);return b},_handleFieldsChange:function(a,b,c){a=[];this.layer&&this.layer.fields&&0<this.layer.fields.length&&(0<this._selCalcField.getOptions().length&&this._selCalcField.removeOption(this._selCalcField.getOptions()),a=this._createIds(this.layer.fields),
b=f.map(this.layer.fields,function(a){return{label:a.name,value:a.name}}),this._selCalcField.addOption(b),this._selCalcField.set("value",this.field));this.fieldsStore=new u({data:a});this.attributeList.set("store",this.fieldsStore)},_handleFieldChange:function(a,b,c){t.set(this._calcFieldLabel,"innerHTML",e.substitute(this.i18n.exprLabel,{fieldName:c}));this.set("helperType",this.helperType);this._setfocus()},_setHelperTypeAttr:function(a){var b;a?this.helperType=a:this.field?(b=f.filter(this.layer.fields,
function(a){return a.name===this.field},this),(this._calcField=b=b[0])&&"esriFieldTypeDate"===b.type?a="date":b&&"esriFieldTypeString"===b.type?a="string":b&&-1!==f.indexOf(["esriFieldTypeSmallInteger","esriFieldTypeInteger","esriFieldTypeSingle","esriFieldTypeDouble"],b.type)&&(a="numeric")):a="string";"string"===a?this._strRadioBtn.set("checked",!0):"numeric"===a?this._numRadioBtn.set("checked",!0):"date"===a&&this._dateRadioBtn.set("checked",!0)},_handleHelperTypeChange:function(a,b,c){this.helpersList.set("query",
{type:c.functionType});"DateType"===c.functionType?this.attributeList.set("query",{type:"esriFieldTypeDate"}):"StrType"===c.functionType?this.attributeList.set("query",{type:"esriFieldTypeString"}):"NumType"===c.functionType&&this.attributeList.set("query",function(a){return-1!==f.indexOf(["esriFieldTypeSmallInteger","esriFieldTypeInteger","esriFieldTypeSingle","esriFieldTypeDouble"],a.type)});this.helpersList.refresh();this.attributeList.refresh()},_handleSelcCalFieldChange:function(a){this.set("field",
a)},_handleRemoveBtnClick:function(){this._exprBox.set("value","");this._setfocus()},_handleAddButtonClick:function(a){A.stop(a);var b={f:"json"},c;this._exprBox.get("value")?(this._handleCloseMsg(),b.calcExpression=x.toJson(this.get("expression")),b.sqlFormat="standard",this.layer.getDefinitionExpression&&this.layer.getDefinitionExpression()?b.where=this.layer.getDefinitionExpression():w.isDefined(this.layer.definitionExpression)&&""!==this.layer.definitionExpression&&(b.where=this.layer.definitionExpression),
r.id.getCredential(this.layer.url+"/calculate").then(d.hitch(this,function(a){b.token=a.token;c=F({url:this.layer.url+"/calculate",content:b},{usePost:!0});this.emit("calculate-start",{calcPromise:c.promise});this._addBtn.set("disabled",!0);this._showLoading();c.then(d.hitch(this,function(a){this._addBtn.set("disabled",!1);this._hideLoading();var c={};d.mixin(c,{calcExpression:x.fromJson(b.calcExpression)[0].sqlExpression,where:b.where,sqlFormat:b.sqlFormat},a);this.emit("calculate-success",c);this.layer.refresh();
this._showMessages(e.substitute(this.i18n.successMsg,{count:a.updatedFeatureCount}),!0);this.closeOnAdd&&this._close()}),d.hitch(this,this._handleErrorResponse))}),d.hitch(this,this._handleErrorResponse))):this._addBtn.set("disabled",!0)},_handleErrorResponse:function(a){this._addBtn.set("disabled",!1);this._hideLoading();this.emit("calculate-error",a);this._showMessages(e.substitute(this.i18n.exprFailedMsg,{expr:this._exprBox.get("value")})+"\x3cbr/\x3e"+a.details.toString())},_handleCloseButtonClick:function(a){A.stop(a);
this._close()},_showTooltip:function(a,b){var c=k.create("label",{innerHTML:b,className:"esriSmallFont",dir:"ltr"});this._isRightToLeft?v.show(c.outerHTML,a,["after"],!0):v.show(c.outerHTML,a,["after"])},_hideTooltip:function(a,b){v.hide(a)},_setfocus:function(){this._exprBox.focus()},_showMessages:function(a,b){t.set(this._bodyNode,"innerHTML",a);y.fadeIn({node:this._errorMessagePane,easing:B.quadIn,onEnd:d.hitch(this,function(){h.set(this._errorMessagePane,{display:""})})}).play();b&&window.setTimeout(d.hitch(this,
this._handleCloseMsg),this._showMsgTimerInterval)},_handleCloseMsg:function(a){a&&a.preventDefault();"none"!==h.get(this._errorMessagePane,"display")&&y.fadeOut({node:this._errorMessagePane,easing:B.quadOut,onEnd:d.hitch(this,function(){h.set(this._errorMessagePane,{display:"none"})})}).play()},validate:function(){var a=!0;this.layer?this.field?this.layer.supportsCalculate?this.layer.userIsAdmin||this.layer.getEditCapabilities().canUpdate||(e.substitute(this.i18n.lyrUpdateCapMsg,{layername:this.layer.name}),
a=!1):(e.substitute(this.i18n.lyrSupportCalMsg,{layername:this.layer.name}),a=!1):a=!1:a=!1;this._addBtn.set("disabled",!a);return a},_validateExpObj:function(a){var b=!0;a||(b=!1);b?this._handleCloseMsg():this._showMessages(void 0);return b},_updateExpression:function(a){var b=this._exprBox.get("cursorPosition"),c=this._exprBox.get("value"),d="",e=0,m,g;this._validateExpObj(a)&&(this._exprStack||(this._exprStack=[]),0<this._exprStack.length&&(g=this._exprStack[this._exprStack.length-1]),b&&c||(b=
[0,0]),"operator"===a.type?(m=" "+a.value+" ",e=m.length):"helper"===a.type?(m=a.value.name,e=-1!==a.value.name.indexOf(",")?a.value.name.indexOf(","):a.value.name.length-1):"field"===a.type&&(d=w.isDefined(g)&&"helper"===g.type&&-1!==g.value.name.indexOf("MOD")&&-1!==f.indexOf(["esriFieldTypeSmallInteger","esriFieldTypeInteger","esriFieldTypeSingle"],a.value.type),g=w.isDefined(g)&&"helper"===g.type&&-1!==g.value.name.indexOf("MOD")&&"esriFieldTypeDouble"===a.value.type,m=this._calcField&&"esriFieldTypeDouble"===
this._calcField.type&&!d&&-1!==f.indexOf(["esriFieldTypeSmallInteger","esriFieldTypeInteger","esriFieldTypeSingle"],a.value.type)?"CAST("+a.value.name+" AS FLOAT)":this._calcField&&-1!==f.indexOf(["esriFieldTypeSmallInteger","esriFieldTypeInteger","esriFieldTypeSingle"],this._calcField.type)&&g?"CAST("+a.value.name+" AS INT)":a.value.name,e=m.length+1),d=c.substring(0,b[0])+m+c.substring(b[1]),this._exprBox.set("value",d),this._exprBox.focus(),this._exprBox.textbox.setSelectionRange&&"number"==typeof this._exprBox.textbox.selectionStart?
(this._exprBox.textbox.setSelectionRange(b[0]+e,b[0]+e),this._exprBox.set("cursorPosition",[b[0]+e,b[0]+e])):(this._setCaretPosition(this._exprBox.textbox,b[0]+e,b[0]+e),this._exprBox.set("cursorPosition",this._getCursorRange(this._exprBox.textbox))),this._setfocus(),this._exprStack.push(a))},_setCaretPosition:function(a,b,c){a.setSelectionRange&&"number"==typeof a.selectionStart?a.setSelectionRange(b,c):"undefined"!=typeof a.createTextRange&&(a=a.createTextRange(),a.collapse(!0),a.moveEnd("character",
c),a.moveStart("character",b),a.select())},_getCaretPosition:function(a){var b=0;if(E.doc.selection)a.focus(),b=E.doc.selection.createRange(),b.moveStart("character",-a.value.length),b=b.text.length;else if(a.selectionStart||"number"==typeof a.selectionStart)b=a.selectionStart;return b},_getCursorRange:function(a){var b,c;a.setSelectionRange&&"number"==typeof a.selectionStart?(b=a.selectionStart,c=a.selectionEnd):"undefined"!=typeof a.createTextRange&&(b=this._getCaretPosition(a),c=this._getCaretPosition(a));
return[b,c]},_handleExpChange:function(a,b,c){this._addBtn.set("disabled",!c);this._validateBtn.set("disabled",!c);this._removeBtn.set("disabled",!c)},_handleValidationBtnClick:function(){var a={sql:this.field+" \x3d "+this._exprBox.get("value"),sqlType:"where",f:"json"},a=F({url:this.layer.url+"/validateSQL",content:a},{usePost:!0});this._addBtn.set("disabled",!0);this._validateBtn.set("disabled",!0);this._showLoading();a.then(d.hitch(this,function(a){var b;this._hideLoading();this._validateBtn.set("disabled",
!1);this._addBtn.set("disabled",!a.isValidSQL);a.isValidSQL?(z.toggle(this._errorMessagePane,this._css.formSuccess,!0),this._handleCloseMsg(),this._showMessages(l.calculateFields.validExpression)):(a.validationErrors&&0<a.validationErrors.length?(b="",f.forEach(a.validationErrors,function(a){if(a.params&&l.calculateFields.errorCodes[a.errorCode]){var c={},d;for(d in a.params)a.params.hasOwnProperty(d)&&(c[d]=a.params[d]);b+=e.substitute(l.calculateFields.errorCodes[a.errorCode],c)+"\x3cbr/\x3e"}else b+=
(l.calculateFields.errorCodes[a.errorCode]||a.description)+"\x3cbr/\x3e"},this),this._showMessages(b,!1)):this._showMessages(l.calculateFields.invalidExpression),z.toggle(this._errorMessagePane,this._css.formSuccess,!1))}),d.hitch(this,function(a){this._hideLoading();this._validateBtn.set("disabled",!1);this._addBtn.set("disabled",!1)}))},_showLoading:function(){h.set(this._underlay,"display","block")},_hideLoading:function(){h.set(this._underlay,"display","none")},_getTypeLabel:function(a){var b;
-1!==f.indexOf(["esriFieldTypeSmallInteger","esriFieldTypeInteger","esriFieldTypeSingle"],a)?b=this.i18n.integerLabel:"esriFieldTypeDouble"===a?b=this.i18n.doubleLabel:"esriFieldTypeDate"===a?b=this.i18n.dateLabel:"esriFieldTypeString"===a&&(b=this.i18n.stringLabel);return b},_setLayerAttr:function(a){this._set("layer",a);this._set("fields",a.fields)},_setFieldsAttr:function(a){this._set("fields",a)},_setFieldAttr:function(a){this._set("field",a)},_setHelperMethodsAttr:function(a){this._set("helperMethods",
a)},_setOperatorsAttr:function(a){this._set("operators",a)},_setShowSelectFieldAttr:function(a){this._set("showSelectField",a)},_setShowHeaderAttr:function(a){this._set("showHeader",a)},_setCloseOnAddAttr:function(a){this._set("closeOnAdd",a)},_getExpressionAttr:function(){var a=this._exprBox.get("value"),b,c;if(a)return a.split(" "),c=[],b={field:this.field},b.sqlExpression=a,c.push(b),c;this._addBtn.set("disabled",!0)},_setExpressionAttr:function(a){this._set("expression",a);this._exprBox&&this._exprBox.set("value",
a)},_setAddButtonClassAttr:function(a){this._set("addButtonClass",a)},_setCloseButtonClassAttr:function(a){this._set("closeButtonClass",a)}});I("extend-esri")&&d.setObject("dijit.CalculateField",n,r);return n});