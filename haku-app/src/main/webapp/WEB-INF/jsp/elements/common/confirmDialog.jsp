<c:if test="${preview}">
    <div id="overlay">
        <div class="popup-dialog-wrapper" id="areyousure" style="z-index:1000;display:none;">
            <span class="popup-dialog-close">&#8203;</span>

            <div class="popup-dialog">
                <span class="popup-dialog-close">&#8203;</span>

                <div class="popup-dialog-header">
                    <h3><fmt:message key="lomake.send.confirm.title"/></h3>
                </div>
                <div class="popup-dialog-content">
                    <form method="post">
                        <p><fmt:message key="lomake.send.confirm.message"/></p>
                        <button name="nav-send" value="true" data-po-hide="areyousure">
								<span>
									<span><fmt:message key="lomake.send.confirm.no"/></span>
								</span>
                        </button>
                        <button id="submit_confirm" class="primary set-right" name="nav-send" type="submit"
                                value="true">
								<span>
									<span><fmt:message key="lomake.send.confirm.yes"/></span>
								</span>
                        </button>
                        <div role="presentation" class="clear"></div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:if>

