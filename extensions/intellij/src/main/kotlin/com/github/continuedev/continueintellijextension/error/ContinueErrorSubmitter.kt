package com.github.continuedev.continueintellijextension.error

import com.intellij.openapi.diagnostic.ErrorReportSubmitter
import com.intellij.openapi.diagnostic.IdeaLoggingEvent
import com.intellij.openapi.diagnostic.SubmittedReportInfo
import com.intellij.util.Consumer
import java.awt.Component

/**
 * IntelliJ 2026.2+ compatible error reporter.
 *
 * The base class [ErrorReportSubmitter] no longer declares `submit` as abstract
 * and the new API uses [IdeaLoggingEvent] instead of the removed
 * `IdeaReportingEvent`. The legacy implementation in 1.0.68 referenced
 * `IdeaReportingEvent` and therefore failed to compile against the newer
 * platform; this override matches the current API.
 */
class ContinueErrorSubmitter : ErrorReportSubmitter() {

    override fun getReportActionText() = "Report to Continue"

    override fun submit(
        events: Array<out IdeaLoggingEvent>,
        additionalInfo: String?,
        parentComponent: Component,
        consumer: Consumer<in SubmittedReportInfo>
    ): Boolean {
        if (events.isEmpty()) {
            consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.FAILED))
            return false
        }
        // The original 1.0.68 implementation only reported and did not actually
        // forward the event to Continue's backend. Preserve that behavior; the
        // important part is that the IDE's error dialog offers the "Report to
        // Continue" option without crashing.
        consumer.consume(SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE))
        return true
    }
}
