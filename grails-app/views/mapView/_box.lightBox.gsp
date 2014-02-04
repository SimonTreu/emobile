<div class="lightBoxWrapper clearfix ${css4fixes}">
    <es:center>
        <div class="lightBox boxWrapper${css}" style="width:${width.toInteger() + 48}px;height:${height.toInteger() + 48}px; left: ${ ( (width.toInteger() + 48) - 981 ) / -2 }px;">
            <div class="tc lb-tc">
                <div class="lc lb-lc">&#160;</div>
                <div class="mc lb-mc" style="width:${width.toInteger()}px;" >&#160;</div>
                <div class="rc lb-rc">&#160;</div>
            </div>
            <div class="boxContentWrapper lb-boxContentWrapper clearfix">
                <div class="boxContent lb-boxContent clearfix">
                    <g:if test="${closeHref}">
                        <es:lightBoxCloseBtn href="${closeHref}" />
                    </g:if>

                    <g:if test="${iframe}">
                        <iframe src="${src}" width="${width + 28}" height="${height}" border="0" frameborder="0" scrolling="no"></iframe>
                    </g:if>
                    <g:else>
                        <div class="html clearfix">
                            <div id="lightBoxContent">
                                <es:htmlOutput value="${html}"/>
                            </div>
                        </div>
                    </g:else>
                </div>
            </div>
            <div class="bc lb-bc">
                <div class="lc lb-lc">&#160;</div>
                <div class="mc lb-mc" style="width:${width.toInteger()}px;">&#160;</div>
                <div class="rc lb-rc">&#160;</div>
            </div>
        </div>
    </es:center>
</div>
<div class="lightBoxBg">&#160;</div>
<g:if test="${isRemote}">
    <script type="text/javascript">
        $(document).ready(function(){
            uiPerformanceFix();
            hideDropDowns();
            ie6BgFix();
        });
    </script>
</g:if>