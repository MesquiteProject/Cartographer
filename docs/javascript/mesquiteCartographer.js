var params = Spry.Utils.getLocationParamsAsObject();

var qsParm = new Array();

qsParm['GettingStartedPanel'] = null;
qsParm['CreditPanel'] = null;

qs();

function qs() {
	var query = window.location.search.substring(1);
	var parms = query.split('&');
	for (var i=0; i<parms.length; i++) {
		var pos = parms[i].indexOf('=');
		if (pos > 0) {
			var key = parms[i].substring(0,pos);
			var val = parms[i].substring(pos+1);
			qsParm[key] = val;
		}
	}
}

parameterString = function()
{
	var paramString = '';
	if (GettingStartedPanel.isOpen()) {
		if (paramString=='') paramString+='?'; else paramString+='&';
		paramString+= 'GettingStartedPanel=open';
	}
	if (CreditPanel.isOpen()) {
		if (paramString=='') paramString+='?'; else paramString+='&';
		paramString+= 'CreditPanel=open';
	}

	return paramString;
};


pageLink = function(page)
{
	document.location.href = page + parameterString();
};

