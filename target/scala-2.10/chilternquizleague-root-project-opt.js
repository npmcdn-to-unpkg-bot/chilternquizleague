(function(){'use strict';
var f="object"===typeof __ScalaJSEnv&&__ScalaJSEnv?__ScalaJSEnv:{},k="object"===typeof f.global&&f.global?f.global:"object"===typeof global&&global&&global.Object===Object?global:this;f.global=k;var l="object"===typeof f.exportsNamespace&&f.exportsNamespace?f.exportsNamespace:k;f.exportsNamespace=l;k.Object.freeze(f);var n={envInfo:f,semantics:{asInstanceOfs:2,moduleInit:2,strictFloats:!1,productionMode:!0},assumingES6:!1,linkerVersion:"0.6.8"};k.Object.freeze(n);k.Object.freeze(n.semantics);
var q=k.Math.imul||function(a,b){var c=a&65535,d=b&65535;return c*d+((a>>>16&65535)*d+c*(b>>>16&65535)<<16>>>0)|0},r=k.Math.clz32||function(a){if(0===a)return 32;var b=1;0===(a&4294901760)&&(a<<=16,b+=16);0===(a&4278190080)&&(a<<=8,b+=8);0===(a&4026531840)&&(a<<=4,b+=4);0===(a&3221225472)&&(a<<=2,b+=2);return b+(a>>31)},s=0,t=k.WeakMap?new k.WeakMap:null;function u(a){return function(b,c){return!(!b||!b.a||b.a.n!==c||b.a.l!==a)}}function aa(a){for(var b in a)return b}
function v(a,b,c){var d=new a.D(b[c]);if(c<b.length-1){a=a.o;c+=1;for(var e=d.B,g=0;g<e.length;g++)e[g]=v(a,b,c)}return d}function ca(a){switch(typeof a){case "string":return w(y);case "number":var b=a|0;return b===a?b<<24>>24===b&&1/b!==1/-0?w(z):b<<16>>16===b&&1/b!==1/-0?w(A):w(B):"number"===typeof a?w(C):w(D);case "boolean":return w(E);case "undefined":return w(F);default:return null===a?a.ga():a&&a.a&&a.a.h.z?w(G):a&&a.a?w(a.a):null}}
function H(a){switch(typeof a){case "string":I||(I=(new J).j());for(var b=0,c=1,d=-1+(a.length|0)|0;0<=d;)b=b+q(65535&(a.charCodeAt(d)|0),c)|0,c=q(31,c),d=-1+d|0;return b;case "number":K||(K=(new L).j());b=K;c=a|0;if(c===a&&-Infinity!==1/a)b=c;else{if(b.i)b.E[0]=a,b=M(N(O(b.t[b.F]|0)),P((new Q).d(-1,0),O(b.t[b.G]|0)));else{if(a!==a)b=!1,a=2047,c=+k.Math.pow(2,51);else if(Infinity===a||-Infinity===a)b=0>a,a=2047,c=0;else if(0===a)b=-Infinity===1/a,c=a=0;else if(d=(b=0>a)?-a:a,d>=+k.Math.pow(2,-1022)){a=
+k.Math.pow(2,52);var c=+k.Math.log(d)/0.6931471805599453,c=+k.Math.floor(c)|0,c=1023>c?c:1023,e=d/+k.Math.pow(2,c)*a,d=+k.Math.floor(e),e=e-d,d=0.5>e?d:0.5<e?1+d:0!==d%2?1+d:d;2<=d/a&&(c=1+c|0,d=1);1023<c?(c=2047,d=0):(c=1023+c|0,d-=a);a=c;c=d}else a=d/+k.Math.pow(2,-1074),c=+k.Math.floor(a),d=a-c,a=0,c=0.5>d?c:0.5<d?1+c:0!==c%2?1+c:c;c=+c;d=c|0;b=M(N(O((b?-2147483648:0)|(a|0)<<20|c/4294967296|0)),P((new Q).d(-1,0),O(d)))}b=b.e^b.f}return b;case "boolean":return a?1231:1237;case "undefined":return 0;
default:return a&&a.a||null===a?a.y():null===t?42:R(a)}}function da(a,b){for(var c=k.Object.getPrototypeOf,d=k.Object.getOwnPropertyDescriptor,e=c(a);null!==e;){var g=d(e,b);if(void 0!==g)return g;e=c(e)}}function ea(a,b,c){a=da(a,c);if(void 0!==a)return c=a.get,void 0!==c?c.call(b):a.value}function fa(a,b,c,d){a=da(a,c);if(void 0!==a&&(a=a.set,void 0!==a)){a.call(b,d);return}throw new k.TypeError("super has no setter '"+c+"'.");}
var R=null!==t?function(a){switch(typeof a){case "string":case "number":case "boolean":case "undefined":return H(a);default:if(null===a)return 0;var b=t.get(a);void 0===b&&(s=b=s+1|0,t.set(a,b));return b}}:function(a){if(a&&a.a){var b=a.$idHashCode$0;if(void 0!==b)return b;if(k.Object.isSealed(a))return 42;s=b=s+1|0;return a.$idHashCode$0=b}return null===a?0:H(a)};this.__ScalaJSExportsNamespace=l;
function S(){this.u=this.D=void 0;this.l=this.o=this.h=null;this.n=0;this.C=null;this.s="";this.c=this.q=this.r=void 0;this.name="";this.isRawJSType=this.isArrayClass=this.isInterface=this.isPrimitive=!1;this.isInstance=void 0}function T(a,b,c){var d=new S;d.h={};d.o=null;d.C=a;d.s=b;d.c=function(){return!1};d.name=c;d.isPrimitive=!0;d.isInstance=function(){return!1};return d}
function U(a,b,c,d,e,g,p){var h=new S,m=aa(a);g=g||function(a){return!!(a&&a.a&&a.a.h[m])};p=p||function(a,b){return!!(a&&a.a&&a.a.n===b&&a.a.l.h[m])};h.u=e;h.h=c;h.s="L"+b+";";h.c=p;h.name=b;h.isInterface=!1;h.isRawJSType=!!d;h.isInstance=g;return h}
function ga(a){function b(a){if("number"===typeof a){this.B=Array(a);for(var b=0;b<a;b++)this.B[b]=e}else this.B=a}var c=new S,d=a.C,e="longZero"==d?ha().w:d;b.prototype=new V;b.prototype.a=c;var d="["+a.s,g=a.l||a,p=a.n+1;c.D=b;c.u=ia;c.h={b:1,ia:1,v:1};c.o=a;c.l=g;c.n=p;c.C=null;c.s=d;c.r=void 0;c.q=void 0;c.c=void 0;c.name=d;c.isPrimitive=!1;c.isInterface=!1;c.isArrayClass=!0;c.isInstance=function(a){return g.c(a,p)};return c}function w(a){if(!a.r){var b=new W;b.p=a;a.r=b}return a.r}
S.prototype.getFakeInstance=function(){return this===y?"some string":this===E?!1:this===z||this===A||this===B||this===C||this===D?0:this===G?ha().w:this===F?void 0:{a:this}};S.prototype.getSuperclass=function(){return this.u?w(this.u):null};S.prototype.getComponentType=function(){return this.o?w(this.o):null};S.prototype.newArrayOfThisClass=function(a){for(var b=this,c=0;c<a.length;c++)b.q||(b.q=ga(b)),b=b.q;return v(b,a,0)};
var ja=T(!1,"Z","boolean"),ka=T(0,"C","char"),la=T(0,"B","byte"),ma=T(0,"S","short"),na=T(0,"I","int"),pa=T("longZero","J","long"),qa=T(0,"F","float"),ra=T(0,"D","double");ja.c=u(ja);ka.c=u(ka);la.c=u(la);ma.c=u(ma);na.c=u(na);pa.c=u(pa);qa.c=u(qa);ra.c=u(ra);function X(){}function V(){}V.prototype=X.prototype;X.prototype.j=function(){return this};X.prototype.A=function(){var a;a=ca(this).p.name;var b=(+(this.y()>>>0)).toString(16);return a+"@"+b};X.prototype.y=function(){return R(this)};X.prototype.toString=function(){return this.A()};var ia=U({b:0},"java.lang.Object",{b:1},void 0,void 0,function(a){return null!==a},function(a,b){var c=a&&a.a;if(c){var d=c.n||0;return!(d<b)&&(d>b||!c.l.isPrimitive)}return!1});X.prototype.a=ia;
function W(){this.p=null}W.prototype=new V;W.prototype.constructor=W;W.prototype.A=function(){return(this.p.isInterface?"interface ":this.p.isPrimitive?"":"class ")+this.p.name};W.prototype.a=U({P:0},"java.lang.Class",{P:1,b:1});function Y(){}Y.prototype=new V;Y.prototype.constructor=Y;function ta(){}ta.prototype=Y.prototype;function L(){this.i=!1;this.E=this.M=this.t=this.m=null;this.x=!1;this.G=this.F=0}L.prototype=new V;L.prototype.constructor=L;
L.prototype.j=function(){K=this;this.m=(this.i=!!(k.ArrayBuffer&&k.Int32Array&&k.Float32Array&&k.Float64Array))?new k.ArrayBuffer(8):null;this.t=this.i?new k.Int32Array(this.m,0,2):null;this.M=this.i?new k.Float32Array(this.m,0,2):null;this.E=this.i?new k.Float64Array(this.m,0,1):null;if(this.i)this.t[0]=16909060,a=1===((new k.Int8Array(this.m,0,8))[0]|0);else var a=!0;this.F=(this.x=a)?0:1;this.G=this.x?1:0;return this};L.prototype.a=U({V:0},"scala.scalajs.runtime.Bits$",{V:1,b:1});var K=void 0;
function J(){this.ba=null;this.fa=!1}J.prototype=new V;J.prototype.constructor=J;J.prototype.j=function(){return this};J.prototype.a=U({X:0},"scala.scalajs.runtime.RuntimeString$",{X:1,b:1});
var I=void 0,F=U({Y:0},"scala.runtime.BoxedUnit",{Y:1,b:1},void 0,void 0,function(a){return void 0===a}),E=U({N:0},"java.lang.Boolean",{N:1,b:1,g:1},void 0,void 0,function(a){return"boolean"===typeof a}),z=U({O:0},"java.lang.Byte",{O:1,k:1,b:1,g:1},void 0,void 0,function(a){return a<<24>>24===a&&1/a!==1/-0}),D=U({Q:0},"java.lang.Double",{Q:1,k:1,b:1,g:1},void 0,void 0,function(a){return"number"===typeof a}),C=U({R:0},"java.lang.Float",{R:1,k:1,b:1,g:1},void 0,void 0,function(a){return"number"===typeof a}),
B=U({S:0},"java.lang.Integer",{S:1,k:1,b:1,g:1},void 0,void 0,function(a){return(a|0)===a&&1/a!==1/-0}),G=U({T:0},"java.lang.Long",{T:1,k:1,b:1,g:1},void 0,void 0,function(a){return!!(a&&a.a&&a.a.h.z)}),A=U({U:0},"java.lang.Short",{U:1,k:1,b:1,g:1},void 0,void 0,function(a){return a<<16>>16===a&&1/a!==1/-0});function Z(){this.Z=this.aa=this.$=this.ea=this.da=this.ca=0;this.H=this.I=this.J=this.K=this.w=null}Z.prototype=new V;Z.prototype.constructor=Z;
Z.prototype.j=function(){$=this;this.w=(new Q).d(0,0);this.K=(new Q).d(1,0);this.J=(new Q).d(-1,-1);this.I=(new Q).d(0,-2147483648);this.H=(new Q).d(-1,2147483647);return this};Z.prototype.a=U({W:0},"scala.scalajs.runtime.RuntimeLong$",{W:1,b:1,ja:1,v:1});var $=void 0;function ha(){$||($=(new Z).j());return $}var y=U({L:0},"java.lang.String",{L:1,b:1,v:1,ha:1,g:1},void 0,void 0,function(a){return"string"===typeof a});function Q(){this.f=this.e=0}Q.prototype=new ta;Q.prototype.constructor=Q;
function M(a,b){return(new Q).d(a.e|b.e,a.f|b.f)}Q.prototype.A=function(){var a=this.e,b=this.f;return b===a>>31?""+a:0>b?"-"+ua(-a|0,0!==a?~b:-b|0):ua(a,b)};Q.prototype.d=function(a,b){this.e=a;this.f=b;return this};function P(a,b){return(new Q).d(a.e&b.e,a.f&b.f)}function N(a){return(new Q).d(0,a.e<<32)}function O(a){var b=new Q;Q.prototype.d.call(b,a,a>>31);return b}Q.prototype.y=function(){return this.e^this.f};
function ua(a,b){if(0===(-2097152&b))return""+(4294967296*b+ +(a>>>0));var c,d=(32+r(1E9)|0)-(0!==b?r(b):32+r(a)|0)|0,e=d;0===e?(c=1E9,e=0):32>e?(c=1E9<<e,e=1E9>>>(-e|0)|0|0<<e):(c=0,e=1E9<<e);for(var g=c,p=e,h=a,m=b,e=c=0;0<=d&&0!==(-2097152&m);){var x=h,ba=m,sa=g,oa=p;if(ba===oa?(-2147483648^x)>=(-2147483648^sa):(-2147483648^ba)>=(-2147483648^oa))x=h,h=x-g|0,m=(m-p|0)+((-2147483648^x)<(-2147483648^h)?-1:0)|0,32>d?c|=1<<d:e|=1<<d;d=-1+d|0;x=p>>>1|0;g=g>>>1|0|p<<-1;p=x}d=m;if(0===d?-1147483648<=(-2147483648^
h):-2147483648<=(-2147483648^d))d=4294967296*m+ +(h>>>0),g=d/1E9,h=c,c=m=h+(g|0)|0,e=(e+(g/4294967296|0)|0)+((-2147483648^m)<(-2147483648^h)?1:0)|0,d%=1E9,h=d|0,m=d/4294967296|0;c=[c,e,h,m];e=""+(c["2"]|0);return""+(4294967296*(c["1"]|0)+ +((c["0"]|0)>>>0))+"000000000".substring(e.length|0)+e}Q.prototype.a=U({z:0},"scala.scalajs.runtime.RuntimeLong",{z:1,k:1,b:1,v:1,g:1});
}).call(this);
//# sourceMappingURL=chilternquizleague-root-project-opt.js.map
