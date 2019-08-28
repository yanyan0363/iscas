// All material copyright ESRI, All Rights Reserved, unless otherwise specified.
// See http://js.arcgis.com/3.20/esri/copyright.txt for details.
//>>built
define("esri/layers/ArcGISMapServiceLayer","dojo/_base/declare dojo/_base/lang dojo/_base/array dojo/has ../kernel ../lang ../request ../SpatialReference ../geometry/Extent ./LayerInfo".split(" "),function(b,c,h,k,l,f,m,n,g,p){b=b(null,{declaredClass:"esri.layers.ArcGISMapServiceLayer",infoTemplates:null,constructor:function(a,b){this.layerInfos=[];b&&(this.infoTemplates=b.infoTemplates||null);var e=this._params={},d=this._url.query?this._url.query.token:null;d&&(e.token=d)},setInfoTemplates:function(a){this.infoTemplates=
a},_load:function(){m({url:this._url.path,content:c.mixin({f:"json"},this._params),callbackParamName:"callback",load:this._initLayer,error:this._errorHandler})},spatialReference:null,initialExtent:null,fullExtent:null,description:null,units:null,_initLayer:function(a,b){try{this._findCredential();(this.credential&&this.credential.ssl||a&&a._ssl)&&this._useSSL();this.description=a.description;this.copyright=a.copyrightText;this.spatialReference=a.spatialReference&&new n(a.spatialReference);this.initialExtent=
a.initialExtent&&new g(a.initialExtent);this.fullExtent=a.fullExtent&&new g(a.fullExtent);this.units=a.units;this.maxRecordCount=a.maxRecordCount;this.maxImageHeight=a.maxImageHeight;this.maxImageWidth=a.maxImageWidth;this.supportsDynamicLayers=a.supportsDynamicLayers;var e=this.layerInfos=[],d=a.layers,c=this._defaultVisibleLayers=[];h.forEach(d,function(a,b){e[b]=new p(a);a.defaultVisibility&&c.push(a.id)});this.visibleLayers||(this.visibleLayers=c);this.version=a.currentVersion;this.version||(this.version=
"capabilities"in a||"tables"in a?10:"supportedImageFormatTypes"in a?9.31:9.3);this.capabilities=a.capabilities;f.isDefined(a.minScale)&&!this._hasMin&&this.setMinScale(a.minScale);f.isDefined(a.maxScale)&&!this._hasMax&&this.setMaxScale(a.maxScale)}catch(q){this._errorHandler(q)}}});k("extend-esri")&&c.setObject("layers.ArcGISMapServiceLayer",b,l);return b});