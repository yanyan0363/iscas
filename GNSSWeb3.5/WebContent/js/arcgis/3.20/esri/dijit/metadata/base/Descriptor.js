// All material copyright ESRI, All Rights Reserved, unless otherwise specified.
// See http://js.arcgis.com/3.20/esri/copyright.txt for details.
//>>built
define("esri/dijit/metadata/base/Descriptor","dojo/_base/declare dojo/_base/lang dojo/_base/array dojo/has ./Templated ../context/DescriptorMixin ../../../kernel".split(" "),function(b,c,d,e,f,g,h){b=b([f,g],{_escapeSingleQuotes:!0,_isGxeDescriptor:!0,_replicas:null,postCreate:function(){this.inherited(arguments);this._replicas=[]},destroy:function(){try{d.forEach(this._replicas,function(a){try{a.destroyRecursive(!1)}catch(k){console.error(k)}})}catch(a){console.error(a)}this._replicas=[];this.inherited(arguments)},
newInstance:function(){var a=new this.constructor;this._replicas.push(a);return a}});e("extend-esri")&&c.setObject("dijit.metadata.base.Descriptor",b,h);return b});