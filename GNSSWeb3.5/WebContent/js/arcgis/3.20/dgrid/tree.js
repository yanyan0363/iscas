//>>built
define("dgrid/tree","dojo/_base/declare dojo/_base/array dojo/_base/Deferred dojo/_base/lang dojo/query dojo/on dojo/aspect ./util/has-css3 ./Grid dojo/has!touch?./util/touch put-selector/put".split(" "),function(E,A,q,B,v,C,f,w,D,x,h){function y(c,f,k,d){d=this.grid.isRTL?"right":"left";var b=".dgrid-expando-icon";f&&(b+=".ui-icon.ui-icon-triangle-1-"+(k?"se":"e"));c=h("div"+b+"[style\x3dmargin-"+d+": "+c*(this.indentWidth||9)+"px; float: "+d+"]");c.innerHTML="\x26nbsp;";return c}function z(c){var f=
this,k=this.style.height;k&&(this.style.display="0px"==k?"none":"block");c&&(h(this,".dgrid-tree-resetting"),setTimeout(function(){h(f,"!dgrid-tree-resetting")}));this.style.height=""}function t(c){var t=c.renderCell||D.defaultRenderCell,k,d;c||(c={});c.shouldExpand=c.shouldExpand||function(b,c,g){return g};f.after(c,"init",function(){var b=c.grid,m=".dgrid-content .dgrid-column-"+c.id,g=[];b.cleanEmptyObservers=!1;if(!b.store)throw Error("dgrid tree column plugin requires a store to operate.");c.renderExpando||
(c.renderExpando=y);g.push(b.on(c.expandOn||".dgrid-expando-icon:click,"+m+":dblclick,"+m+":keydown",function(a){var c=b.row(a);b.store.mayHaveChildren&&!b.store.mayHaveChildren(c.data)||"keydown"==a.type&&32!=a.keyCode||"dblclick"==a.type&&d&&1<d.count&&c.id==d.id&&-1<a.target.className.indexOf("dgrid-expando-icon")||b.expand(c);-1<a.target.className.indexOf("dgrid-expando-icon")&&(d&&d.id==b.row(a).id?d.count++:d={id:b.row(a).id,count:1})}));w("touch")&&g.push(b.on(x.selector(m,x.dbltap),function(){b.expand(this)}));
b._expanded||(b._expanded={});g.push(f.after(b,"insertRow",function(a){var b=this.row(a);c.shouldExpand(b,k,this._expanded[b.id])&&this.expand(a,!0,!0);return a}));g.push(f.before(b,"removeRow",function(a,c){var e=a.connected;e&&(v("\x3e.dgrid-row",e).forEach(function(a){b.removeRow(a,!0)}),c||h(e,"!"))}));c.collapseOnRefresh&&g.push(f.after(b,"cleanup",function(){this._expanded={}}));b._calcRowHeight=function(a){var b=a.connected;return a.offsetHeight+(b?b.offsetHeight:0)};b.expand=function(a,e,
g){var d=a.element?a:b.row(a),f=w("transitionend");a=new q;var k=a.promise;a.resolve();a=d.element;a=-1<a.className.indexOf("dgrid-expando-icon")?a:v(".dgrid-expando-icon",a)[0];g=g||!1===c.enableTransitions;if(a&&a.mayHaveChildren&&(g||e!==!!this._expanded[d.id])){var n=void 0===e?!this._expanded[d.id]:e;h(a,".ui-icon-triangle-1-"+(n?"se":"e")+"!ui-icon-triangle-1-"+(n?"e":"se"));e=d.element;var l=e.connected,p,m,u={originalQuery:this.query};if(!l){var l=u.container=e.connected=h(e,"+div.dgrid-tree-container"),
r=function(a){return b.store.getChildren(d.data,a)};c.allowDuplicates&&(u.parentId=d.id);"level"in a&&(r.level=a.level);k=k.then(function(){if(b.renderQuery)return b.renderQuery(r,u);var a=h(l,"div"),c=b.renderArray(r(u),a,"level"in r?{queryLevel:r.level}:{});q.when(c,function(){h(a,"!")});return c});f?C(l,f,z):z.call(l)}l.hidden=!n;p=l.style;!f||g?(p.display=n?"block":"none",p.height=""):(n?(p.display="block",m=l.scrollHeight,p.height="0px"):(h(l,".dgrid-tree-resetting"),p.height=l.scrollHeight+
"px"),setTimeout(function(){h(l,"!dgrid-tree-resetting");p.height=n?m?m+"px":"auto":"0px"}));n?this._expanded[d.id]=!0:delete this._expanded[d.id]}return k};f.after(c,"destroy",function(){A.forEach(g,function(a){a.remove()});delete b.expand;delete b._calcRowHeight})});c.renderCell=function(b,d,g,a){var e=c.grid,f=Number(a&&a.queryLevel)+1,m=!e.store.mayHaveChildren||e.store.mayHaveChildren(b),q=a.parentId,f=k=isNaN(f)?0:f,e=c.renderExpando(f,m,e._expanded[(q?q+"-":"")+e.store.getIdentity(b)],b);e.level=
f;e.mayHaveChildren=m;(b=t.call(c,b,d,g,a))&&b.nodeType?(h(g,e),h(g,b)):g.insertBefore(e,g.firstChild)};return c}t.defaultRenderExpando=y;B.getObject("dgrid.tree",!0);return dgrid.tree=t});