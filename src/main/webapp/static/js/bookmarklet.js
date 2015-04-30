(function(){
	var baseUrl = 'https://tlrlapp.com';
	//var baseUrl = 'http://localhost:8080';
	try {
		/* simple test to detect SCP. If enabled, we should open 
		 * TLRL in new window, as our scripts are probably blacklisted */
		new Function('');
	} catch(e) {
		window.location.href= baseUrl + '/bm/add?url=' + encodeURIComponent(window.location.href);
		return;
	}

	/* Looks like security policy allows external scripts to be loaded. 
	 * But check if we've loaded already. */
	if(!window.tlrlPost) {
		var url = window.location.href;
			el = document.createElement("script");

		window.__tlrlBaseUrl = baseUrl;	
		el.src=baseUrl + "/static/js/bookmarklet-sender.js?" +(new Date).getTime();
		document.body.appendChild(el);
	} else {
		/* Call function that will handle bookmarking this page */
		window.tlrlPost();
	}
})();
