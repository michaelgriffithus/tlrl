(function(e,a,h,f,c,b,d) { 
	/* Make sure we have jQuery loaded */
	if(!(f=e.jQuery) || h(f)) {
		c=a.createElement("script");
		c.type="text/javascript";
		c.src=window.__tlrlBaseUrl + "/static/lib/jquery/dist/jquery.min.js";
		c.onload = c.onreadystatechange = function() {
			if(!b && (!(d=this.readyState) || d=="loaded" || d=="complete")) {
				h((f=e.jQuery).noConflict(1),b=1);
				f(c).remove();
			}
		};
		for(n in a.documentElement.childNodes) {
			var node = a.documentElement.childNodes[n]
			if(node.hasOwnProperty("nodeName") && node.nodeName === 'HEAD') {
				node.appendChild(c);
			}
		}
	}
})(window,document,function($,L) {
	/* Common properties for our alert/msg divs */
	var tlrlMsgStyle = 'display:none;font-size:16px;position:fixed;top:10px;right:10px;z-index:100000;border-radius:3px;width:320px;padding:10px 10px 20px 10px;border: 1px solid #ddd;';	

	/* Main container holding all tlrl related content */
	$('<div id="tlrl-container"></div>').appendTo('body'); 

		/* Success message to display after save */
	var tlrlSuccess = $('<div id="tlrl-success" style="'+ tlrlMsgStyle +'background-color:#fcf8e3;"></div>').appendTo('#tlrl-container'),
		/* Login message to display if 403 error was received */
		tlrlLogin = $('<div id="tlrl-login" style="'+ tlrlMsgStyle +'background-color:#fff;">' +
					'<i style="color:#999;font:normal 600 18px/1.8em monospace;text-decoration:none;">tlrl</i><br/>' +
					'Please <a href="'+ window.__tlrlBaseUrl +'" target="__tlrl" class="tlrl-hide">signin</a> to tlrl before bookmarking.<br/>' +
					'<span style="color:#aaa;font-size:13px;">(Note: will open in a new tab/window.)</span><br/><div style="float:right; margin-right:20px;">[<a class="tlrl-hide" style="cursor:pointer">close</a>]</div></div>')
				.appendTo('#tlrl-container'),
		/* An overlay to show with login message */
		tlrlOverlay = $('<div id="tlrl-overlay" class="tlrl-hide" style="display:none;position:fixed;left:0%;right:0%;top:0%;bottom:0%;z-index:99999;background:#111;opacity:0.5;"></div>').appendTo('#tlrl-container'),
		/* iframe containing receiver code for our postMessage cross domain communication */
		tlrlFrame = $('<iframe id="tlrl-frame" style="display:none;" src="' + window.__tlrlBaseUrl + '/bookmarklet-receiver.jsp?r=' + new Date().getTime() + '"></iframe>').appendTo('#tlrl-container');

	/* Show the login message */
	function tlrlShowLogin(show) {
		tlrlOverlay.toggle(show);
		tlrlLogin.toggle(show);
	}

	var tlrlTimeoutId;

	/* Show the success message */
	function tlrlShowSuccess(show) {
		tlrlSuccess.text(window.location.href + ' has been successfully bookmarked!');
		tlrlSuccess.toggle(show);
		/* let's get rid of this message after 3 secs */
		tlrlTimeoutId = setTimeout(function() {
			tlrlHideMsg();
		}, 3000)
	}

	/* Helper for hiding all messages */
	function tlrlHideMsg() {
		tlrlShowSuccess(false);		
		tlrlShowLogin(false);
		clearTimeout(tlrlTimeoutId)
	}

	/* Handles responses from TLRL bookmarklet receiver */
	function tlrlHandleEvent(e) {
		/* proceed if it came from same originating url set in our bookmarklet */
		if(e.origin === window.__tlrlBaseUrl) {
			var attrs = {}
			/* we are getting data in the form n1=v1&n2=v2... 
			 * parse and dump into attrs hash. Specifically we 
			 * want url and title of bookmarked page.
			 */
			e.data.split('&').forEach(function(el, i, arr) {
				var attr = el.split('=')
				attrs[attr[0]] = attr[1];
			})

			if(attrs.status === '403') {
				tlrlShowLogin(true);
			} else {
				tlrlShowSuccess(true);
			}
		}
	}

	/* Sends our "add readlater" message to TLRL bookmarklet receiver. 
	 * Note, we assign it to the parent window so that subsequent clicks
	 * of bookmarklet can skip reloading this sender and just call tlrlPost.
	 */
	window.tlrlPost = function () {
		console.log(encodeURIComponent($(document).attr('body')))
		tlrlFrame[0].contentWindow.postMessage('tlrl=addReadLater&url=' +
				encodeURIComponent(window.location.href) + '&content=' +
				encodeURIComponent($(document).text().replace(/\s{2,}/g, ' ')) + '&title=' +
				encodeURIComponent($(document).attr('title')), '*');
	}

	window.addEventListener('message', tlrlHandleEvent, false);

	tlrlFrame.load(function() {
		//hide the messages on display
		$('.tlrl-hide').on('click', function() {
			tlrlHideMsg();
		});
		//hide the messages on display 
		$(document).keyup(function(e) {
		  if (e.keyCode == 27) { //esc
		  	tlrlHideMsg();
		  } 
		});
		//post our request
		window.tlrlPost();
	})

});