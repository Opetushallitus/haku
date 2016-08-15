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
                        <p id="areyousure_question"><fmt:message key="lomake.send.confirm.message"/></p>
                        <button aria-labelledby="areyousure_question areyousure_question_no" name="nav-send" value="true" data-po-hide="areyousure">
                            <span>
                                <span id="areyousure_question_no"><fmt:message key="lomake.send.confirm.no"/></span>
                            </span>
                        </button>
                        <button aria-labelledby="areyousure_question areyousure_question_yes" id="submit_confirm" class="primary set-right" name="nav-send" type="submit" value="true">
                            <span>
                                <span id="loading_button_text" style="display: none;"><fmt:message key="lomake.tallennetaan" /></span>
                                <span id="areyousure_question_yes"><fmt:message key="lomake.send.confirm.yes"/></span>
                            </span>
                        </button>
                        <div role="presentation" class="clear"></div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</c:if>

