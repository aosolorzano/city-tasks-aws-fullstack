package com.hiperium.city.tasks.api.configurations.hints;

import org.hibernate.query.CommonQueryContract;
import org.hibernate.query.SelectionQuery;
import org.hibernate.query.hql.spi.SqmQueryImplementor;
import org.hibernate.query.spi.DomainQueryExecutionContext;
import org.hibernate.query.sqm.internal.SqmInterpretationsKey;
import org.springframework.aot.hint.ProxyHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class HibernateProxyHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        ProxyHints proxies = hints.proxies();
        proxies.registerJdkProxy(SqmQueryImplementor.class, SqmInterpretationsKey.InterpretationsKeySource.class,
                DomainQueryExecutionContext.class, SelectionQuery.class, CommonQueryContract.class);
    }
}
