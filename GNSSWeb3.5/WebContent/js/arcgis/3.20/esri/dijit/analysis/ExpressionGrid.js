// All material copyright ESRI, All Rights Reserved, unless otherwise specified.
// See http://js.arcgis.com/3.20/esri/copyright.txt for details.
//>>built
require({cache:{"url:esri/dijit/analysis/templates/ExpressionGrid.html":'\x3cdiv class\x3d"esriAnalysisExpressionGrid"\x3e\r\n  \x3cdiv data-dojo-type\x3d"dijit.layout.ContentPane" style\x3d"padding:1px;width:100%;height:200px;"  data-dojo-attach-point\x3d"_gridPane"\x3e\r\n    \x3cdiv data-dojo-attach-point\x3d"_gridDiv" style\x3d"height:90%"\x3e\x3c/div\x3e\r\n    \x3cdiv data-dojo-attach-point\x3d"_textDiv" style\x3d"border:1px #EFEEEF solid;display:none;height:90%"\x3e\x3c/div\x3e\r\n  \x3c/div\x3e\r\n  \x3cdiv style\x3d"clear:both;"\x3e\x3c/div\x3e\r\n  \x3cdiv data-dojo-type\x3d"dijit.layout.ContentPane" style\x3d"padding:1px;width:100%;"\x3e\r\n    \x3ctable style\x3d"width:100%;"\x3e\r\n      \x3ctbody\x3e\r\n        \x3ctr\x3e\r\n          \x3ctd style\x3d"width:20%;"\x3e\r\n            \x3cdiv data-dojo-type\x3d"dijit/form/Button" class\x3d"esriTertiaryActionBtn calcite green tiny" data-dojo-attach-point\x3d"_addBtn" data-dojo-attach-event\x3d"onClick:_handleAddButtonClick" class\x3d""\x3e\r\n              ${i18n.addExpr}\r\n            \x3c/div\x3e\r\n          \x3c/td\x3e\r\n          \x3ctd class\x3d"esriFloatTrailing"\x3e\r\n            \x3cdiv class\x3d"esriLeadingMargin025"\x3e\r\n              \x3cdiv data-dojo-type\x3d"dijit/form/Button" class\x3d"esriActionButton" data-dojo-props\x3d"label:\'${i18n.edit}\',iconClass:\'esriAnalysisEditIcon\',showLabel: false, disabled:true" data-dojo-attach-point\x3d"_editBtn" data-dojo-attach-event\x3d"onClick:_handleEditButtonClick"\x3e\x3c/div\x3e\r\n              \x3cdiv data-dojo-type\x3d"dijit/form/Button" class\x3d"esriActionButton" data-dojo-props\x3d"label:\'${i18n.remove}\',iconClass:\'esriAnalysisRemoveIcon\',showLabel: false, disabled:true" data-dojo-attach-point\x3d"_removeBtn" data-dojo-attach-event\x3d"onClick:_handleRemoveButtonClick"\x3e\x3c/div\x3e     \r\n              \x3cdiv data-dojo-type\x3d"dijit/form/Button" class\x3d"esriActionButton" data-dojo-props\x3d"label:\'${i18n.groupLabel}\',iconClass:\'esriAnalysisGroupIcon\',showLabel: false, disabled:true" data-dojo-attach-point\x3d"_groupBtn" data-dojo-attach-event\x3d"onClick:_handleGroupButtonClick"\x3e\x3c/div\x3e\r\n              \x3cdiv data-dojo-type\x3d"dijit/form/Button" class\x3d"esriActionButton" data-dojo-props\x3d"label:\'${i18n.ungroupLabel}\',iconClass:\'esriAnalysisUngroupIcon\',showLabel: false, disabled:true" data-dojo-attach-point\x3d"_ungroupBtn" data-dojo-attach-event\x3d"onClick:_handleUngroupButtonClick"\x3e\x3c/div\x3e\r\n              \x3cdiv data-dojo-type\x3d"dijit/form/ToggleButton" class\x3d"esriActionButton" data-dojo-props\x3d"label:\'${i18n.viewText}\',iconClass:\'esriAnalysisTextIcon\',showLabel: false, checked: false, disabled:true" data-dojo-attach-point\x3d"_viewBtn" data-dojo-attach-event\x3d"onChange:_handleViewButtonClick"\x3e\x3c/div\x3e           \r\n            \x3c/div\x3e\r\n          \x3c/td\x3e\r\n        \x3c/tr\x3e\r\n      \x3c/tbody\x3e\r\n      \x3c/table\x3e\r\n  \x3c/div\x3e\r\n  \x3cdiv data-dojo-type\x3d"dijit/Dialog" title\x3d"${i18n.expression}" data-dojo-props\x3d"closable:false" data-dojo-attach-point\x3d"_expDialog" style\x3d"width:65em;"\x3e\r\n    \x3c!--\x3cdiv data-dojo-attach-point\x3d"_testdiv"\x3etesting\x3c/div\x3e--\x3e\r\n    \x3cdiv data-dojo-attach-point\x3d"_expressionForm" data-dojo-type\x3d"esri/dijit/analysis/ExpressionForm" data-dojo-props\x3d"primaryActionButttonClass:\'${primaryActionButttonClass}\'"\x3e\x3c/div\x3e\r\n  \x3c/div\x3e   \r\n\x3c/div\x3e'}});
define("esri/dijit/analysis/ExpressionGrid","require dojo/_base/declare dojo/_base/lang dojo/_base/array dojo/_base/connect dojo/_base/json dojo/has dojo/json dojo/string dojo/dom-style dojo/dom-attr dojo/dom-construct dojo/query dojo/dom-class dojo/store/Memory dojo/store/Observable dojo/Evented dojo/_base/event dojo/window dijit/_WidgetBase dijit/_TemplatedMixin dijit/_WidgetsInTemplateMixin dijit/_OnDijitClickMixin dijit/_FocusMixin dijit/registry dijit/form/Button dijit/form/CheckBox dijit/form/Form dijit/form/Select dijit/form/ToggleButton dijit/form/TextBox dijit/form/ValidationTextBox dijit/layout/ContentPane dijit/Dialog dijit/InlineEditBox dgrid/OnDemandGrid dgrid/Keyboard dgrid/Selection dgrid/selector dgrid/extensions/DijitRegistry dgrid/util/mouse ./tree put-selector/put ../../kernel ../../lang ./ExpressionForm dojo/i18n!../../nls/jsapi dojo/text!./templates/ExpressionGrid.html".split(" "),
function(q,u,g,d,P,Q,y,v,R,k,r,l,S,T,z,A,B,t,C,D,E,F,G,H,U,V,W,X,Y,Z,aa,ba,ca,da,ea,I,J,K,fa,L,ga,w,M,N,m,ha,p,O){var x;x=u([w,I,K,J,L]);q=u([D,E,F,G,H,B],{declaredClass:"esri.dijit.analysis.ExpressionGrid",templateString:O,widgetsInTemplate:!0,indentWidth:10,refreshOptions:{keepScrollPosition:!0},allowAllInputOperands:!1,_selectedIds:[],constructor:function(b){b.containerNode&&(this.container=b.containerNode)},destroy:function(){this.inherited(arguments)},postMixInProperties:function(){this.inherited(arguments);
this.i18n={};g.mixin(this.i18n,p.common);g.mixin(this.i18n,p.analysisTools);g.mixin(this.i18n,p.analysisMsgCodes);g.mixin(this.i18n,p.expressionGrid)},postCreate:function(){this.inherited(arguments);var b;this.expressionStore=A(new z({idProperty:"id",allExpressionText:"",data:[{id:0,operator:"",expressionText:""}],getChildren:function(a){return this.query({parent:a.id})},getAllChildren:function(a){var b,e;b=this.getChildren(a);0<b.total&&d.forEach(b,function(a){e=this.getAllChildren(a);0<e.total&&
d.forEach(e,function(a){b[b.total]=a;b.total+=1},this)},this);return b},getExpressions:function(a,b){var c,h,f,n,g;c=this.getChildren(a);0<c.total?(f=[],n={},n.operator=a.operator,n.layer=a.layer,a.where?n.where=a.where:(n.selectingLayer=a.selectingLayer,n.spatialRel=a.spatialRel,a.distance&&(n.distance=a.distance,n.units=a.units)),-1===this.allExpressionText.indexOf(a.text)&&(this.allExpressionText+=a.operator+" ( "+a.text+" "),f.push(n),d.forEach(c,function(a,b){h=this.getExpressions(a,b===c.total-
1?!0:!1);g=this.getChildren(a);0<g.total&&(a._isAdd||(this.allExpressionText+=")"),a._isAdd=!0);f.push(h)},this)):(f={},f.operator=a.operator,f.layer=a.layer,a.where?f.where=a.where:(f.selectingLayer=a.selectingLayer,f.spatialRel=a.spatialRel,a.distance&&(f.distance=a.distance,f.units=a.units)),-1===this.allExpressionText.indexOf(a.text)&&(this.allExpressionText+=a.operator+" "+a.text+" ",b&&!0===b&&(this.allExpressionText+=")")));return f},getLabel:function(a){return a.text},mayHaveChildren:function(a){return 1!==
a.id}}));b={operator:w({label:"",renderCell:g.hitch(this,this._renderExpOperatorCell),shouldExpand:function(){return!0},sortable:!1,indentWidth:10,renderExpando:function(a,b,e,h){a=M("div.dgrid-expando-icon[style\x3dwidth:0;height:0;]");a.innerHTML=" ";return a}})};this.expressiongrid=new x({store:this.expressionStore,query:{expressionText:""},selectionMode:"extended",columns:b,showHeader:!1,allowSelectAll:!0,allowSelect:function(a){return a&&a.data&&0===a.data.id?!1:!0}},this._gridDiv);this.expressiongrid.on("dgrid-select",
g.hitch(this,this._handleExpressiongridSelect));this.expressiongrid.startup();this.expressiongrid.keepScrollPosition=!0;this.allowAllInputOperands?this._expressionForm.set("firstOperands",this.inputLayers):this._expressionForm.set("firstOperands",[this.analysisLayer]);this._expressionForm.set("selectedFirstOperand",this.analysisLayer);this._expressionForm.set("inputOperands",this.inputLayers);this._expressionForm.set("showReadyToUseLayers",this.get("showReadyToUseLayers"));this._expressionForm.set("showReadyLayersForFirst",
this.allowAllInputOperands);this._expressionForm.set("owningWidget",this.get("owningWidget"));this._expressionForm.init();this._expressionForm.on("add-expression",g.hitch(this,this._handleExpressionFormAdd));this._expressionForm.on("cancel-expression",g.hitch(this,this._handleExpressionFormCancel))},_handleExpressiongridSelect:function(b){var a,c;if((this._selectedObj=b.grid.selection)&&this._selectedIds&&0<this._selectedIds.length){c=!0;b=this._selectedIds.toString();for(a in this._selectedObj)this._selectedObj.hasOwnProperty(a)&&
(a=parseInt(a,10),c=-1!==b.indexOf(a));if(c)return}this._selectedIds=[];this._selectedRows=[];for(a in this._selectedObj)this._selectedObj.hasOwnProperty(a)&&(a=parseInt(a,10),!0===this._selectedObj[a]&&0!==a&&(this._selectedIds.push(a),this._selectedRows.push(this.expressiongrid.cell(a).row),b=this.expressiongrid.cell(a).row.data,b=this.expressionStore.getAllChildren(b),0<b.total&&d.forEach(b,function(a){this._selectedIds.push(a.id);this._selectedRows.push(this.expressiongrid.cell(a.id).row);this.expressiongrid.select(a.id)},
this)),!0===this._selectedObj[a]&&0===a&&(this._groupBtn.set("disabled",!0),this._ungroupBtn.set("disabled",!0),this._removeBtn.set("disabled",!0),this._editBtn.set("disabled",!0),this._addBtn.set("disabled",!1),this._viewBtn.set("disabled",!0)));0<this._selectedIds.length&&(a=1===this._selectedIds.length,this._groupBtn.set("disabled",a||3>=this.expressionStore.data.length),this._ungroupBtn.set("disabled",a||3>=this.expressionStore.data.length),this._removeBtn.set("disabled",!1),this._editBtn.set("disabled",
!a),this._addBtn.set("disabled",!a),this._viewBtn.set("disabled",!1))},_renderExpOperatorCell:function(b,a,c,e){if(!b.expressionText)1===this.expressionStore.data.length?l.create("label",{innerHTML:this.i18n.addExprDescription,style:{fontStyle:"italic",textAlign:"center",display:"inline-block",width:"105%",fontWeight:"lighter"}},c):k.set(c,"display","none");else if(b.expressionText){var h,f,d,m;m=0;f=this._gridPane.isRTL?"marginRight":"marginLeft";h=l.create("table",{"class":"esriExpressionTable"},
c);1<e.level?(d=e.level*e.level*this.indentWidth+8+"px",k.set(h,f,d)):k.set(h,f,"5px");h=l.create("tr",{},h);f=this.expressionStore.getAllChildren(b);0<e.level&&0<f.total&&(m=1===e.level?2*this.indentWidth*e.level+24:this.indentWidth*e.level*2+this.indentWidth);e=l.create("td",{"class":"expressionTd"},h);k.set(e,"width","32px");f=l.create("td",{"class":"expressionTd"},h);k.set(f,"width",m+"px");f=l.create("div",{},f);k.set(f,"width",m+"px");a?l.create("div",{innerHTML:this.i18n[a],name:b.operator,
id:b.id,"class":"esriAnalysisOperatorButton",onclick:g.hitch(this,function(a){t.stop(a);a=a.target;var b,c;b=this.expressionStore.get(a.id);this.expressiongrid.clearSelection();this._selectedRows=[];this._selectedIds=[];c=r.get(a,"name");a.innerHTML="and"===c?this.i18n.or:this.i18n.and;r.set(a,"name","and"===c?"or":"and");b.operator="and"===c?"or":"and";this.expressionStore.put(b);this.expressiongrid.refresh(this.refreshOptions)})},e,"first"):l.create("div",{style:"width:32px;"},e);a=l.create("td",
{"class":"esriAnalysisExpression expressionTd"},h);l.create("label",{"class":"",title:this.expressionStore.getLabel(b),innerHTML:b.expressionText},a)}return c},_clear:function(){this._selectedIds=[];this.expressiongrid.clearSelection();this.expressiongrid.refresh(this.refreshOptions);this._groupBtn.set("disabled",!0);this._ungroupBtn.set("disabled",!0);this._removeBtn.set("disabled",!0);this._editBtn.set("disabled",!0);this._addBtn.set("disabled",!1);this._viewBtn.set("disabled",1===this.expressionStore.data.length?
!0:!1);1===this.expressionStore.data.length&&(this.allowAllInputOperands?this._expressionForm.set("firstOperands",this.inputLayers):this._expressionForm.set("firstOperands",[this.analysisLayer]),this._expressionForm.set("selectedFirstOperand",this.analysisLayer),this._expressionForm.set("inputOperands",this.inputLayers))},_handleAddButtonClick:function(b){this._expDialog.set("title",this.i18n.addExpr);this._expressionForm.set("action","add");this._expressionForm.clear();this._expDialog.show()},_handleEditButtonClick:function(b){t.stop(b);
this._expDialog.set("title",this.i18n.editExpr);if(this._selectedIds&&0===this._selectedIds.length)return!1;b=this.expressionStore.get(this._selectedIds[0]);this._expressionForm.set("action","edit");this._expressionForm.clear();this._expressionForm.set("expression",b);this._expDialog.show()},_handleRemoveButtonClick:function(b){t.stop(b);if(this._selectedIds&&0===this._selectedIds.length)return!1;d.forEach(this._selectedIds,function(a){this.expressionStore.remove(a)},this);this._clear();this.emit("update-expressions",
this.expressionStore.query())},_handleGroupButtonClick:function(b){var a,c;if(this._selectedIds&&0===this._selectedIds.length)return!1;b=d.map(this._selectedIds,function(a){return parseInt(a,10)});a=this._arrayMin(b);d.forEach(this._selectedRows,function(b,d){c=this.expressiongrid.cell(this._selectedRows[d].id);1<this._selectedRows[d].id&&this._selectedRows[d].id!==a&&(0===c.row.data.parent&&this.expressionStore.mayHaveChildren(this.expressionStore.get(a))&&(c.row.data.parent=a),this.expressionStore.put(c.row.data));
this.expressiongrid.refresh(this.refreshOptions)},this);this._clear()},_handleUngroupButtonClick:function(b){var a,c;if(this._selectedIds&&0===this._selectedIds.length)return!1;b=d.map(this._selectedIds,function(a){return parseInt(a,10)});a=this._arrayMin(b);d.forEach(this._selectedRows,function(b,d){c=this.expressiongrid.cell(this._selectedRows[d].id);1<this._selectedRows[d].id&&this._selectedRows[d].id!==a&&(c.row.data.parent===a&&(c.row.data.parent=0),this.expressionStore.put(c.row.data));this.expressiongrid.refresh(this.refreshOptions)},
this);this._clear()},_handleExpressionFormAdd:function(b){var a={},c;"add"===b.action?(a={id:this.expressionStore.data.length,operator:1===this.expressionStore.data.length?"":"and"},0===this._selectedIds.length?a.parent=0:1===this._selectedIds.length&&(c=parseInt(this._selectedIds[0],10),a.parent=this.expressionStore.mayHaveChildren(this.expressionStore.get(c))?c:0)):(a=this.expressionStore.get(parseInt(this._selectedIds[0],10)),a.where&&b.expression.spatialRel&&delete a.where,a.spatialRel&&b.expression.where&&
(delete a.spatialRel,delete a.selectingLayer,a.distance&&(delete a.distance,delete a.units)));g.mixin(a,b.expression);a.expressionText=b.displayText;a.text=b.text;this.expressionStore.put(a);this.expressiongrid.refresh(this.refreshOptions);this._expDialog.hide();this.allowAllInputOperands||this._updateFirstOperands(b.expression);this._clear();this.validate();this.emit("update-expressions",this.expressionStore.query())},_handleExpressionFormCancel:function(){this._expDialog.hide();this._clear()},_handleViewButtonClick:function(b){this._viewBtn.set("label",
b?this.i18n.viewGrid:this.i18n.viewText);this._viewBtn.set("iconClass",b?"esriAnalysisGridIcon":"esriAnalysisTextIcon");b&&(this._groupBtn.set("disabled",b),this._ungroupBtn.set("disabled",b),this._removeBtn.set("disabled",b),this._editBtn.set("disabled",b),this._addBtn.set("disabled",b));this.get("expressions");k.set(this._textDiv,"display",b?"":"none");k.set(this._gridDiv,"display",b?"none":"");r.set(this._textDiv,"innerHTML",b?this.expressionStore.allExpressionText:"");b||this._clear()},_updateFirstOperands:function(b){b=
this.get("selectedLayers");this._expressionForm.set("firstOperands",b)},_getInputLayerById:function(b){d.forEach(this.inputLayers,function(a){if(a.id===b)return a},this)},_arrayMin:function(b){return Math.min.apply(Math,b)},_setInputLayersAttr:function(b){this.inputLayers=b},_getInputLayersAttr:function(){return this.inputLayers},_setAnalysisLayerAttr:function(b){this.analysisLayer=b},_getAnalysisLayerAttr:function(){return this.analysisLayer},_setSelectedLayersAttr:function(b){this.selectedLayers=
b},_getSelectedLayersAttr:function(){var b=[],a=[];d.forEach(this.expressionStore.data,function(c,e){m.isDefined(c.layer)&&-1===d.indexOf(b,c.layer)&&(b.push(c.layer),a.push(this.inputLayers[c.layer]));m.isDefined(c.selectingLayer)&&-1===d.indexOf(b,c.selectingLayer)&&(b.push(c.selectingLayer),a.push(this.inputLayers[c.selectingLayer]))},this);this.selectedLayers=a;this.set("selectedLayerIds",b);return this.selectedLayers},_setSelectedLayerIdsAttr:function(b){this.selectedLayerIds=b},_getSelectedLayerIdsAttr:function(){return this.selectedLayerIds},
_getSelectedLayersMapAttr:function(){var b={};b.inputLayers=this.get("inputLayers");b.selectedLayers=this.get("selectedLayers");b.selectedLayerIds=this.get("selectedLayerIds");return b},_getExpressionsAttr:function(){var b=[],a,c;this.expressionStore.allExpressionText="";d.forEach(this.expressionStore.data,function(a,b){a._isAdd=!1});d.forEach(this.expressionStore.data,function(e,d){0!==d&&(c={},c.operator=e.operator,c.layer=e.layer,e.where?c.where=e.where:(c.selectingLayer=e.selectingLayer,c.spatialRel=
e.spatialRel,e.distance&&(c.distance=e.distance,c.units=e.units)),this._findElementInMultiArray(b,c)||(a=this.expressionStore.getExpressions(e),b.push(a)))},this);return b},_getExpressionMapAttr:function(){var b,a;b=this.get("selectedLayersMap");a=this.get("expressions");console.log(this.expressionStore.allExpressionText);a=this._updateExpressions(a,b);return{expressions:a,inputLayers:b.selectedLayers,expressionString:this.expressionStore.allExpressionText}},_setShowReadyToUseLayersAttr:function(b){this._set("showReadyToUseLayers",
b)},_setOwningWidgetAttr:function(b){this._set("owningWidget",b)},_updateExpressions:function(b,a){d.forEach(b,function(b,e){b instanceof Array||b.length?b=this._updateExpressions(b,a):(m.isDefined(b.layer)&&-1!==d.indexOf(a.selectedLayerIds,b.layer)&&(b.layer=d.indexOf(a.selectedLayerIds,b.layer)),m.isDefined(b.selectingLayer)&&-1!==d.indexOf(a.selectedLayerIds,b.selectingLayer)&&(b.selectingLayer=d.indexOf(a.selectedLayerIds,b.selectingLayer)))},this);return b},_findElementInMultiArray:function(b,
a){var c=!1;d.forEach(b,function(b,d){if(v.stringify(a)===v.stringify(b))return c=!0;if(b instanceof Array||b.length)c=this._findElementInMultiArray(b,a)},this);return c},_setPrimaryActionButttonClassAttr:function(b){this.primaryActionButttonClass=b},_getCssClassesAttr:function(){return this.primaryActionButttonClass},validate:function(){var b;(b=1!==this.expressionStore.data.length)?k.set(this.expressiongrid.domNode,"borderColor","#bba"):(C.scrollIntoView(this.expressiongrid.domNode),this.expressiongrid.focus(),
k.set(this.expressiongrid.domNode,"borderColor","#f94"));return b}});y("extend-esri")&&g.setObject("dijit.analysis.ExpressionGrid",q,N);return q});