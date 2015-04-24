(function(){
	//var baseUrl = 'https://tlrlapp.com';
	var baseUrl = 'http://localhost:8080';
	try {
		/* Simple test to detect CSP. If enabled, we should open TLRL app 
		 * into a new window, as our scripts are probably blacklisted */
		new Function('');
	} catch(e) {
		window.location.href= baseUrl + '/bm/add?url=' + encodeURIComponent(window.location.href);
		return;
	}

	/* If we made it this far, current URL's CSP allows external 
	 * scripts to be loaded. Try to load our scripts if they've
	 * haven't already been loaded. */
	if(!window.tlrlAddBookmark) { 
		var url = window.location.href;
			el = document.createElement("script");

		window.__tlrlBaseUrl = baseUrl;	
		el.src=baseUrl + "/scripts/bookmarklet-sender.js?" +(new Date).getTime();
		document.body.appendChild(el);
	} else { 
		/* We've loaded our scripts before, just call the function that will handle bookmarking this page's URL */
		window.tlrlAddBookmark();
	}
})();
