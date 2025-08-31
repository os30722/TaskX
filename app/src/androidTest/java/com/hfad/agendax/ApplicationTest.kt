package com.hfad.agendax

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    WorkFlowTest::class,
    HomeFragmentTest::class,
    NotificationTest::class
)
class ApplicationTest