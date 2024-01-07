FROM devopsfaith/krakend:2.5.0 as builder
ARG DEPLOY_STAGE=hmg

COPY config /etc/krakend/

RUN FC_ENABLE=1 \
    FC_OUT=/tmp/krakend.json \
    FC_SETTINGS="/etc/krakend/settings/$DEPLOY_STAGE" \
    FC_TEMPLATES="/etc/krakend/templates" \
    krakend check -d -t -c /etc/krakend/krakend.tmpl --lint

FROM devopsfaith/krakend:2.5.0

RUN addgroup krakend
COPY --from=builder --chown=krakend:krakend /etc/krakend/plugin ./plugin
COPY --from=builder --chown=krakend:krakend /tmp/krakend.json .
USER krakend

EXPOSE 8080