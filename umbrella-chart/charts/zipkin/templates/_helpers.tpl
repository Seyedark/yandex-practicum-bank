{{/*
Expand the name of the chart.
*/}}
{{- define "zipkin.name" -}}
{{- default .Chart.Name  }}
{{- end }}