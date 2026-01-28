{{/*
Expand the name of the chart.
*/}}
{{- define "logstash.name" -}}
{{- default .Chart.Name  }}
{{- end }}