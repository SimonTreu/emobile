<label class="inputLabeled ${css}" for="${id}">
    <span class="label">
        <es:htmlOutput value="${label}"/>
    </span>
    <span class="input">
        <span class="left">&#160;</span>
        <span class="inputWrapper clearfix">
            <input type="${type}" value="<es:htmlOutput value="${value}" />" tabindex="${tabindex}" id="${id}" name="${name}"
                <g:if test="${onchange}"> onchange="${onchange}"</g:if>
                <g:if test="${onkeyup}"> onkeyup="${onkeyup}"</g:if>
            />
        </span>
        <span class="right">&#160;</span>
    </span>
</label>
