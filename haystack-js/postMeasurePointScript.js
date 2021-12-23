/**
 * decode demo
 *
 * meta: {
 *      orgId: "{{orgId}}",
 *      netCompType: "http",
 *      http-uri: "/postMeasurepoint",
 *      protocolGateway: "{{protocol-gateway-id}}"
 *  }
 *
 * originalReq: [TYPE] Json
 */
function getProtocol() {
    return 'haystack';
}

function encodeResponse(meta, originalReq, response) {
    return JSON.stringify(response);
}

function decode(meta, originalReq) {
    const jsonOriginalReq = JSON.parse(originalReq);
    const ignoreInvalidMeasurePoint = jsonOriginalReq.ignoreInvalidMeasurePoint ?
        jsonOriginalReq.ignoreInvalidMeasurePoint : false;
    const realtime = jsonOriginalReq.realtime ? jsonOriginalReq.realtime : true;
    const points = jsonOriginalReq.points;

    const now = new Date().getTime();

    const collector = {};
    points.forEach(point => {
        const externalDeviceId = getExternalDeviceId(point.siteRef, point.equipRef);
        const time = point.time ? point.time : now;
        const navName = point.navName;

        if (point.curVal) {
            const normalizedVal = normalizeVal(point.kind, point.curVal);
            if (collector[externalDeviceId]) {
                if (collector[externalDeviceId][time]) {
                    collector[externalDeviceId][time][navName] = normalizedVal;
                } else {
                    collector[externalDeviceId][time] = {
                        [navName]: normalizedVal
                    }
                }
            } else {
                collector[externalDeviceId] = {
                    [time]: {
                        [navName]: normalizedVal
                    }
                }
            }
        }
    });

    const postMps = [];
    Object.keys(collector).forEach(externalDeviceId => {
        Object.keys(collector[externalDeviceId]).forEach(time => {
            postMps.push({
                externalDeviceId,
                measurepoints: collector[externalDeviceId][time],
                time
            })
        })
    })

    return {
        messageType: "PostMeasurePoint",
        ignoreInvalidMeasurePoint,
        realtime,
        measurepoints: postMps
    };
}

function translateHaystackType(kind) {
    if ('Str' === kind) {
        return 'TEXT';
    } else if ('Number' === kind) {
        return 'FLOAT';
    } else if ('Bool' === kind) {
        return 'BOOL';
    }
}

function normalizeVal(kind, curVal) {
    const type = translateHaystackType(kind);
    if ('BOOL' === type) {
        return Boolean(curVal);
    }

    if ('FLOAT' === type) {
        return parseFloat(curVal);
    }

    return curVal;
}

function getExternalDeviceId(site, equipId) {
    return site ? site + '-' + equipId : equipId;
}
