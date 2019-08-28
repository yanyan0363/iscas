var inSt, overSt, outSt=[];
var greySymbol;
var greenSymbol;
var redSymbol;
var totalNum=0;//台站数量
var inlineNum=0;//在线台站数量
var overNum=0;//异常台站数量
require(["esri/symbols/PictureMarkerSymbol"], function(PictureMarkerSymbol)  
{
	greySymbol = new PictureMarkerSymbol("img/trip-32-grey.png", 16, 16);
	greenSymbol = new PictureMarkerSymbol("img/trip-32-blue.png", 16, 16);
	redSymbol = new PictureMarkerSymbol("img/trip-32-orange.png", 16, 16);
	epiSymbol = new PictureMarkerSymbol("img/bling.gif", 16, 16);
}); 

var curStation;
var stationArray = new Array();
var map, mapCenter,baseLayer;  
var layer = new AMap.TileLayer({
    zooms:[3,20],    //可见级别
    visible:true,    //是否可见
    opacity:1,       //透明度
    zIndex:0         //叠加层级
})
var map = new AMap.Map('map',{
    layers:[layer],//当只想显示标准图层时layers属性可缺省
    center:[104,30.5]
});
