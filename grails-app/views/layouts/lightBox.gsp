<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="de" xml:lang="de">
<head>
    <title>title</title>
</head>

<body class="lightBoxBody">
<%-- <ks:flashMessage /> --%>
<div id="lightBoxContent" class="clearfix ${lightBoxClass}">
    <g:layoutBody />
</div>
<%-- <ks:urchinTracker/> --%>
<script type="text/javascript">
    $(document).ready(function(){
        lightBoxHeightFix();
        tabFocusFix();
    });
</script>
</body>
</html>