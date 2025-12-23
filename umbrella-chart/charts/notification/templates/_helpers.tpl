{{/*
Expand the name of the chart.
*/}}
{{- define "notification.name" -}}
{{- default .Chart.Name  }}
{{- end }}