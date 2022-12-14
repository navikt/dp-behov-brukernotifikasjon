package no.nav.dagpenger.behov.brukernotifikasjon.tjenester.rivers

import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import no.nav.dagpenger.behov.brukernotifikasjon.notifikasjoner.Oppgave
import no.nav.dagpenger.behov.brukernotifikasjon.tjenester.Ettersendinger
import no.nav.helse.rapids_rivers.JsonMessage
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.net.URL
import java.util.*
import kotlin.test.assertContains

internal class EttersendingOppgaveRiverTest {
    private val ettersendinger = mockk<Ettersendinger>(relaxed = true)
    private val rapid by lazy {
        TestRapid().apply {
            EttersendingOppgaveRiver(this, ettersendinger, URL("https://dummyUrl"))
        }
    }

    init {
        System.setProperty("brukernotifikasjon.oppgave.topic", "data")
    }

    @AfterEach
    fun cleanUp() {
        rapid.reset()
    }

    @Test
    fun `skal publisere brukernotifikasjoner`() {
        rapid.sendTestMessage(ettersendelseoppgaveBehov.toJson())

        val opprettetOppgave = slot<Oppgave>()

        verify {
            ettersendinger.opprettOppgave(capture(opprettetOppgave))
        }

        assertContains(opprettetOppgave.captured.getSnapshot().link.toString(), søknadId.toString())
    }
}

private val søknadId = UUID.randomUUID()

val ettersendelseoppgaveBehov = JsonMessage.newNeed(
    behov = listOf("OppgaveOmEttersending"),
    map = mapOf(
        "ident" to "12312312312",
        "søknad_uuid" to søknadId
    )
)
