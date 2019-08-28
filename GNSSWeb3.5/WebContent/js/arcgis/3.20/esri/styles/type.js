// All material copyright ESRI, All Rights Reserved, unless otherwise specified.
// See http://js.arcgis.com/3.20/esri/copyright.txt for details.
//>>built
define("esri/styles/type","dojo/_base/array dojo/_base/lang dojo/has ../kernel ../Color ./colors".split(" "),function(l,n,r,t,g,u){function p(b,a){return l.map(b,function(b){b=new g(b);null!=a&&(b.a=a);return b})}function q(b,a,f){var c;if(b=u[b])switch(c={},c.colors=p(b.stops),c.noDataColor=new g(a.noDataColor),c.opacity=a.fillOpacity||1,f){case "point":c.outline={color:new g(a.outline.color),width:a.outline.width};c.size=a.size;break;case "line":c.width=a.width;break;case "polygon":c.outline={color:new g(a.outline.color),
width:a.outline.width}}return c}function v(b){"esriGeometryPoint"===b||"esriGeometryMultipoint"===b?b="point":"esriGeometryPolyline"===b?b="line":"esriGeometryPolygon"===b&&(b="polygon");return b}var h={color:[153,153,153,1],width:1},e="tropical-bliss desert-blooms under-the-sea vibrant-rainbow ocean-bay prairie-summer pastel-chalk".split(" "),k={"default":{name:"default",label:"Default",description:"Default theme for visualizing features by their type.",basemapGroups:{light:"streets gray topo terrain national-geographic oceans osm".split(" "),
dark:["satellite","hybrid","dark-gray"]},pointSchemes:{light:{common:{noDataColor:"#aaaaaa",outline:h,size:8},primary:"cat-dark",secondary:["cat-light"].concat(e)},dark:{common:{noDataColor:"#aaaaaa",outline:{color:[26,26,26,1],width:1},size:8},primary:"cat-light",secondary:["cat-dark"].concat(e)}},lineSchemes:{light:{common:{noDataColor:"#aaaaaa",width:2},primary:"cat-dark",secondary:["cat-light"].concat(e)},dark:{common:{noDataColor:"#aaaaaa",width:2},primary:"cat-light",secondary:["cat-dark"].concat(e)}},
polygonSchemes:{light:{common:{noDataColor:"#aaaaaa",outline:h,fillOpacity:.8},primary:"cat-dark",secondary:["cat-light"].concat(e)},dark:{common:{noDataColor:"#aaaaaa",outline:{color:[51,51,51,1],width:1},fillOpacity:.8},primary:"cat-light",secondary:["cat-dark"].concat(e)}}}},m={};(function(){var b,a,f,c,d,g,e,h;for(b in k)for(c in a=k[b],f=a.basemapGroups,d=m[b]={basemaps:[].concat(f.light).concat(f.dark),point:{},line:{},polygon:{}},f)for(g=f[c],e=0;e<g.length;e++)h=g[e],a.pointSchemes&&(d.point[h]=
a.pointSchemes[c]),a.lineSchemes&&(d.line[h]=a.lineSchemes[c]),a.polygonSchemes&&(d.polygon[h]=a.polygonSchemes[c])})();h={getAvailableThemes:function(b){var a=[],f,c,d;for(f in k)c=k[f],d=m[f],b&&-1===l.indexOf(d.basemaps,b)||a.push({name:c.name,label:c.label,description:c.description,basemaps:d.basemaps.slice(0)});return a},getSchemes:function(b){var a=b.theme,f=b.basemap,c=v(b.geometryType);b=m[a];var d,e;(d=(d=b&&b[c])&&d[f])&&(e={primaryScheme:q(d.primary,d.common,c),secondarySchemes:l.map(d.secondary,
function(a){return q(a,d.common,c)})});return e},cloneScheme:function(b){var a;b&&(a=n.mixin({},b),a.colors=p(a.colors),a.noDataColor&&(a.noDataColor=new g(a.noDataColor)),a.outline&&(a.outline={color:a.outline.color&&new g(a.outline.color),width:a.outline.width}));return a}};r("extend-esri")&&n.setObject("styles.type",h,t);return h});