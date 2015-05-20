	<script type="text/javascript" src="/static/lib/jquery/dist/jquery.min.js"></script>
	<script type="text/javascript">

		var _tlrlHeaders = {
				"${_csrf.headerName}":"${_csrf.token}"	
		}
		
		var handleEvent = function(e) {
			var attrs = {}
			/* we are getting data in the form n1=v1&n2=v2... 
			 * parse and dump into attrs hash. Specifically we 
			 * want url and title of bookmarked page.
			 */
			e.data.split('&').forEach(function(el, i, arr) {
				var attr = el.split('=');
				attrs[attr[0]] = attr[1];
			})
			/* if we found a tlrl "key", then we can hopefully 
			 * assume it's our request 
			 */
			if(attrs.tlrl) {
				
				$.ajax({
					type: 'POST',
					url: '/api/urls',
					data: JSON.stringify({
						url: decodeURIComponent(attrs.url),
						content: decodeURIComponent(attrs.content),
						title: decodeURIComponent(attrs.title)}),
					contentType: "application/json; charset=utf-8",
					dataType: 'json',
					headers: _tlrlHeaders,
					success: function(resp) {
						/* send back an 'OK' status if save was successful. An id of
						 * saved readLater should be good enough indicator of success.
						 * Otherwise, not sure what happen, but send back a 500. 
						 * TODO: better handling of server error.
						 */
						e.source.postMessage((resp.id ? "status=200" : "status=500"), e.origin);
					},
					error: function(resp) {
						/* send back error and let sender handle, this is most likely 403 */
						e.source.postMessage("status=" + resp.status, e.origin);
					}
				});
			}
		}
	
		window.addEventListener('message', handleEvent, false);
	
	</script>
