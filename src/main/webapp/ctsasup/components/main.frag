<div style="width: 100%; height: 100%; background-color: white; color: #333; margin: 5px">
<div id="main-content"></div>
I'm a component so this link can redirect the whole page: <a href="http://ci.uchicago.edu/">ci.uchicago.edu</a>
</div>
<script type="text/javascript">
$(document).ready(function() {
    $.getJSON("http://ctsa-test.ci.uchicago.edu:8080/HelloWorld/helloworld?callback=?",function(data) {
        $('#main-content').html(data);
      });
  });
</script>
