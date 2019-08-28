// All material copyright ESRI, All Rights Reserved, unless otherwise specified.
// See http://js.arcgis.com/3.20/esri/copyright.txt for details.
//>>built
define("esri/layers/RasterLayer","dojo/_base/declare dojo/_base/lang dojo/_base/connect dojo/_base/array dojo/sniff dojo/dom-construct dojo/dom-style dojo/number ../lang ../domUtils ./BaseRasterLayer ./ImageServiceLayerMixin ./pixelFilters/StretchFilter ../SpatialReference ../geometry/Point".split(" "),function(d,h,k,l,m,n,p,q,r,t,e,f,g,u,v){return d([e,f],{declaredClass:"esri.layers.RasterLayer",constructor:function(b,a){this.pixelData=null;null!==this.format&&void 0!==this.format||this.setImageFormat("LERC",
!0)},_setDefaultFilter:function(){if(this.loaded&&this.drawType&&!this._isVectorData()&&(!this.pixelFilter||this._isDefaultPixelFilter))if("jpeg"===this.format.toLowerCase()||"jpg"===this.format.toLowerCase()||-1<this.format.toLowerCase().indexOf("png"))this._isDefaultPixelFilter&&(this.pixelFilter=null,this._isDefaultPixelFilter=!1);else{var b,a;if(this.minValues&&this.maxValues&&this.stdvValues&&this.meanValues){b=[];for(a=0;a<this.minValues.length;a++)b.push([this.minValues[a],this.maxValues[a],
this.meanValues[a],this.stdvValues[a]]);this.bandCount!==b.length&&(b=null)}a=0;var c=!1;"U8"===this.pixelType?(a=5,c=b?!1:!0):b?(a=5,c=!1):(a=6,c=!0);this.renderingRule&&(a=5,c=!0);this.pixelFilter=(new g({stretchType:a,min:0,max:255,dra:c,minPercent:.25,maxPercent:.25,useGamma:!1,computeGamma:!1,statistics:b,numberOfStandardDeviations:2.5})).filter;this._isDefaultPixelFilter=!0}}})});